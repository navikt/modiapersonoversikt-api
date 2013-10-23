package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import no.nav.sbl.dialogarena.sporsmalogsvar.Traad;

import no.nav.sbl.dialogarena.sporsmalogsvar.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume.Traader;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.panel.JournalforPanel;
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
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.validation.validator.StringValidator;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.events.Events.KVITTERING;

public class TraadPanel extends Panel {

    @Inject
    private HenvendelsePortType henvendelsePortType;
    @Inject
    private BesvareHenvendelsePortType besvareHenvendelsePortType;

    private Traader traader = new Traader(besvareHenvendelsePortType, henvendelsePortType);

    private MarkupContainer sisteMelding;
    private Dialog dialog;

    private String oppgaveId;

    private final IModel<Traad> traad = new AbstractReadOnlyModel<Traad>() {
        @Override
        public Traad getObject() {
            return (Traad) getDefaultModelObject();
        }
    };

    private final IModel<Melding> siste = new AbstractReadOnlyModel<Melding>() {
        @Override
        public Melding getObject() {
            return traad.getObject().getSisteMelding();
        }
    };

    public TraadPanel(String id, final String fnr) {
        super(id);
        setOutputMarkupId(true);
        setDefaultModel(new CompoundPropertyModel<>(new LoadableDetachableModel<Traad>() {
            @Override
            protected Traad load() {
                return traader.hentTraad(fnr, oppgaveId)
                        .getOrThrow(new AbortWithHttpErrorCodeException(404, "Fant ikke henvendelse for oppgaveid = " + oppgaveId));
            }
        }));


        sisteMelding = new WebMarkupContainer("siste-melding").add(
                new Label("overskrift", new MeldingOverskrift(siste, traad)),
                new Label("sisteMelding.sendtDato"),
                new MultiLineLabel("sisteMelding.fritekst"));

        dialog = new Dialog("tidligereDialog");
        add(
                new JournalforPanel("journalfor-panel", traad.getObject()),
                new SvarForm("svar"),
                sisteMelding,
                dialog);
    }


    private Traad getTraad() {
        return traad.getObject();
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(TraadPanel.class, "besvare.js")));
    }

    private final class SvarForm extends Form<Traad.Svar> {

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
                    new Label("tema", new StringResourceModel("${tema}", TraadPanel.this.getDefaultModel())),
                    new CheckBox("erSensitiv"),
                    feedbackPanel,
                    fritekst,
                    new AjaxSubmitLink("send") {

                        @Override
                        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                            traader.besvareSporsmal(getTraad());
                            SvarForm.this.setVisibilityAllowed(false);
                            dialog.setList(getTraad().getDialog());
                            sisteMelding.setVisibilityAllowed(false);

                            send(getPage(), Broadcast.DEPTH, KVITTERING);

                            target.add(TraadPanel.this);
                        }

                        @Override
                        protected void onError(AjaxRequestTarget target, Form<?> form) {
                            target.add(feedbackPanel);
                        }
                    }
            );
        }

    }


    private class Dialog extends PropertyListView<Melding> {

        public Dialog(String id) {
            super(id);
            setReuseItems(true);
        }

        @Override
        protected void populateItem(ListItem<Melding> meldingItem) {
            meldingItem.add(
                    new Label("sendtDato"),
                    new Label("overskrift", new MeldingOverskrift(meldingItem.getModel(), traad)),
                    new MultiLineLabel("fritekst"));
            meldingItem.add(hasCssClassIf("tidligere-dialog", meldingItem.getModelObject().tidligereHenvendelse));
        }

    }


    public void besvar(String oppgaveId) {
        this.oppgaveId = oppgaveId;
    }
}