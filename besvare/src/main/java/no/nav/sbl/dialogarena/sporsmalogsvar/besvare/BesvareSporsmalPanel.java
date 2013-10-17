package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareService;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Svar;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Traad;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import org.apache.wicket.MarkupContainer;
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

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.events.Events.KVITTERING;

public class BesvareSporsmalPanel extends Panel {

    @Inject
    private HenvendelsePortType henvendelsePortType;
    @Inject
    private BesvareHenvendelsePortType besvareHenvendelsePortType;

    private BesvareService service = new BesvareService(besvareHenvendelsePortType, henvendelsePortType);

    private MarkupContainer sisteMelding;
    private Dialog dialog;

    private String oppgaveId;

    public BesvareSporsmalPanel(String id, final String fnr) {
        super(id);
        setOutputMarkupId(true);
        setDefaultModel(new CompoundPropertyModel<>(new LoadableDetachableModel<Traad>() {
            Traad traad = new Traad();
            @Override
            protected Traad load() {
                traad = service.hentTraad(fnr, oppgaveId)
                        .getOrThrow(new AbortWithHttpErrorCodeException(404, "Fant ikke henvendelse for oppgaveid = " + oppgaveId))
                        .merge(traad);
                return traad;
            }
        }));

        sisteMelding = new WebMarkupContainer("siste-melding")
            .add(new Label("sisteMelding.overskrift"), new Label("sisteMelding.sendtDato"), new MultiLineLabel("sisteMelding.fritekst"));

        dialog = new Dialog("tidligereDialog");
        add(
                new SvarForm("svar"),
                sisteMelding,
                dialog);
    }

    private Traad getTraad() {
        return (Traad) getDefaultModelObject();
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

            TextArea<String> fritekst = new TextArea<>("svar.fritekst");
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
                            service.besvareSporsmal(getTraad());
                            SvarForm.this.setVisibilityAllowed(false);
                            dialog.setList(getTraad().getDialog());
                            sisteMelding.setVisibilityAllowed(false);

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

    private static class Dialog extends PropertyListView<Melding> {

        public Dialog(String id) {
            super(id);
            setReuseItems(true);
        }

        @Override
        protected void populateItem(ListItem<Melding> item) {
            item.add(
                    new Label("sendtDato"),
                    new Label("overskrift"),
                    new MultiLineLabel("fritekst"));
            item.add(hasCssClassIf("tidligere-dialog", item.getModelObject().tidligereHenvendelse));
        }

    }

    public void besvar(String oppgaveId) {
        this.oppgaveId = oppgaveId;
    }
}