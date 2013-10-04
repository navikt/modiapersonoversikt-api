package no.nav.sbl.dialogarena.sporsmalogsvar.web.besvare;

import no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareService;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareSporsmalDetaljer;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Henvendelse;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Sporsmal;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Svar;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.validation.validator.StringValidator;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.INNGAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.UTGAENDE;

public class BesvareSporsmalPanel extends Panel {

    @Inject
    private BesvareService service;

    private SporsmalDetaljer sporsmalDetaljer;
    private TidligereDialog tidligereDialog;

    private String oppgaveId;

    public BesvareSporsmalPanel(String id, final String fnr) {
        super(id);
        setOutputMarkupId(true);

        LoadableDetachableModel<BesvareSporsmalDetaljer> besvareSporsmalDetaljer = new LoadableDetachableModel<BesvareSporsmalDetaljer>() {
            @Override
            protected BesvareSporsmalDetaljer load() {
                return service.hentDetaljer(fnr, oppgaveId);
            }
        };
        setDefaultModel(new CompoundPropertyModel<Object>(besvareSporsmalDetaljer));

        sporsmalDetaljer = new SporsmalDetaljer("sporsmal");
        tidligereDialog = new TidligereDialog("tidligereDialog");
        add(
                new SvarForm("svar"),
                sporsmalDetaljer,
                tidligereDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
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
                    new Label("tema"),
                    new CheckBox("svar.sensitiv"),
                    feedbackPanel,
                    fritekst,
                    new AjaxSubmitLink("send") {

                        @Override
                        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                            Svar svar = getModelObject();
                            service.besvareSporsmal(svar);

                            Sporsmal sporsmal = (Sporsmal) sporsmalDetaljer.getDefaultModelObject();
                            tidligereDialog.prependHenvendelse(new Henvendelse(INNGAENDE, sporsmal.getSendtDato(), sporsmal.getFritekst()));

                            Henvendelse svarkvittering = new Henvendelse(UTGAENDE, DateTime.now(), svar.fritekst);
                            svarkvittering.tidligereHenvendelse.setObject(false);
                            tidligereDialog.prependHenvendelse(svarkvittering);

                            sporsmalDetaljer.setVisible(false);
                            SvarForm.this.setVisible(false);

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
            add(new Label("sporsmal.sendtDatoAsString"), new Label("sporsmal.fritekst"));
        }
    }

    private static class TidligereDialog extends PropertyListView<Henvendelse> {

        public TidligereDialog(String id) {
            super(id);
        }

        @Override
        protected void populateItem(ListItem<Henvendelse> item) {
            item.add(new Label("sendtDato"), new Label("overskrift"), new Label("fritekst"));
            item.add(hasCssClassIf("tidligere-dialog", item.getModelObject().tidligereHenvendelse));
        }

        public void prependHenvendelse(Henvendelse henvendelse) {
            List<Henvendelse> henvendelser = new ArrayList<>(getModelObject());
            henvendelser.add(0, henvendelse);
            setModelObject(henvendelser);
        }
    }

    public void besvar(String oppgaveId) {
        this.oppgaveId = oppgaveId;
    }
}