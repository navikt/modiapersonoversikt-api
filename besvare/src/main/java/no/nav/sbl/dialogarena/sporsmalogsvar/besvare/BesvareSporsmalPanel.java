package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareService;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareSporsmalDetaljer;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Henvendelse;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Sporsmal;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Svar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.validation.validator.StringValidator;
import org.joda.time.DateTime;

import javax.inject.Inject;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.events.Events.KVITTERING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.INNGAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.UTGAENDE;

public class BesvareSporsmalPanel extends Panel {

    @Inject
    private HenvendelsePortType henvendelsePortType;
    @Inject
    private BesvareHenvendelsePortType besvareHenvendelsePortType;

    private BesvareService service = new BesvareService(besvareHenvendelsePortType, henvendelsePortType);

    private SporsmalDetaljer sporsmalDetaljer;
    private TidligereDialog tidligereDialog;

    private String oppgaveId;

    public BesvareSporsmalPanel(String id, final String fnr) {
        super(id);
        setOutputMarkupId(true);
        setDefaultModel(new CompoundPropertyModel<>(new LoadableDetachableModel<BesvareSporsmalDetaljer>() {
            @Override
            protected BesvareSporsmalDetaljer load() {
                Optional<BesvareSporsmalDetaljer> detaljer = service.hentDetaljer(fnr, oppgaveId);
                return detaljer.getOrThrow(new AbortWithHttpErrorCodeException(404, "Fant ikke henvendelse for oppgaveid = " + oppgaveId));
            }
        }));

        sporsmalDetaljer = new SporsmalDetaljer("sporsmal");
        tidligereDialog = new TidligereDialog("tidligereDialog");
        add(
                new SvarForm("svar"),
                sporsmalDetaljer,
                tidligereDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(BesvareSporsmalPanel.class, "besvare.js")));
    }

    private final class SvarForm extends Form<Svar> {

        private static final int FRITEKST_MAKS_LENGDE = 5000;

        public SvarForm(String id) {
            super(id);
            setOutputMarkupId(true);

            final TextArea<String> fritekst = new TextArea<>("svar.fritekst");
            fritekst.setRequired(true);
            fritekst.add(StringValidator.maximumLength(FRITEKST_MAKS_LENGDE));

            final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
            feedbackPanel.setOutputMarkupId(true);

            add(
                    new Label("tema", new StringResourceModel("${tema}", BesvareSporsmalPanel.this.getDefaultModel())),
                    new CheckBox("svar.sensitiv"),
                    feedbackPanel,
                    fritekst,
                    new AjaxSubmitLink("send") {

                        @Override
                        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                            Svar svar = getModelObject();
                            service.besvareSporsmal(svar);

                            Sporsmal sporsmal = sporsmalDetaljer.getSporsmal();
                            tidligereDialog.prependHenvendelse(new Henvendelse(INNGAENDE, sporsmal.sendtDato, sporsmal.fritekst));

                            Henvendelse svarkvittering = new Henvendelse(UTGAENDE, DateTime.now(), svar.fritekst);
                            svarkvittering.tidligereHenvendelse.setObject(false);
                            tidligereDialog.prependHenvendelse(svarkvittering);

                            sporsmalDetaljer.setVisibilityAllowed(false);
                            SvarForm.this.setVisibilityAllowed(false);

                            send(getPage(), Broadcast.DEPTH, KVITTERING);

                            target.add(BesvareSporsmalPanel.this);
                        }

                        @Override
                        protected void onError(AjaxRequestTarget target, Form<?> form) {
                            target.add(feedbackPanel);
                        }
                    }
            );
        }
    }

    private static class SporsmalDetaljer extends WebMarkupContainer {

        public SporsmalDetaljer(String id) {
            super(id);
            add(new Label("sporsmal.sendtDatoAsString"), new MultiLineLabel("sporsmal.fritekst"));
        }

        public Sporsmal getSporsmal() {
            return (Sporsmal) getDefaultModelObject();
        }
    }

    private static class TidligereDialog extends PropertyListView<Henvendelse> {

        public TidligereDialog(String id) {
            super(id);
        }

        @Override
        protected void populateItem(ListItem<Henvendelse> item) {
            item.add(new Label("sendtDato"), new Label("overskrift"), new MultiLineLabel("fritekst"));
            item.add(hasCssClassIf("tidligere-dialog", item.getModelObject().tidligereHenvendelse));
        }

        public void prependHenvendelse(Henvendelse henvendelse) {
            setModelObject(on(getModelObject()).prepend(henvendelse).collect());
        }
    }

    public void besvar(String oppgaveId) {
        this.oppgaveId = oppgaveId;
    }
}