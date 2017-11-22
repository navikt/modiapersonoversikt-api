package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel;

import no.nav.metrics.Timer;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.feedbackform.FeedbackLabel;
import no.nav.modig.wicket.errorhandling.aria.AriaFeedbackPanel;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;
import java.io.Serializable;

import static no.nav.metrics.MetricsFactory.createTimer;
import static no.nav.brukerdialog.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.brukerdialog.security.tilgangskontroll.utils.AttributeUtils.resourceId;
import static no.nav.modig.modia.security.WicketAutorizationUtils.accessRestriction;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.*;
import static org.apache.wicket.ajax.attributes.AjaxRequestAttributes.EventPropagation;
import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

public class PlukkOppgavePanel extends Panel {

    public static final String TEMAGRUPPE_ATTR = "sos-temagruppe";

    @Inject
    private PlukkOppgaveService plukkOppgaveService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    private final IModel<Temagruppe> valgtTemagruppe;
    private final AriaFeedbackPanel feedbackPanel;
    private final Label velgtemagruppeKnapp;

    public PlukkOppgavePanel(String id) {
        super(id);

        add(accessRestriction(RENDER).withAttributes(actionId("plukkoppgave"), resourceId("")));

        valgtTemagruppe = new Model<>((Temagruppe) getSession().getAttribute(TEMAGRUPPE_ATTR));
        Form<Temagruppe> form = new Form<>("plukkOppgaveForm", valgtTemagruppe);
        form.setOutputMarkupId(true);


        velgtemagruppeKnapp = new Label("velg-temagruppe-knapp");
        velgtemagruppeKnapp.setOutputMarkupId(true);

        final RadioGroup radioGroup = new RadioGroup<>("temagruppe", valgtTemagruppe);
        radioGroup.setRenderBodyOnly(false);
        radioGroup.setRequired(true);
        radioGroup.setOutputMarkupPlaceholderTag(true);

        radioGroup.add(new ListView<Temagruppe>("temagrupper", Temagruppe.PLUKKBARE) {
            @Override
            protected void populateItem(ListItem<Temagruppe> item) {
                item.add(new Radio<>("temagruppevalg", item.getModel()));
                item.add(new Label("temagruppenavn", new ResourceModel(item.getModelObject().name())));
            }
        });

        feedbackPanel = new AriaFeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this) {
            @Override
            public boolean accept(FeedbackMessage message) {
                return super.accept(message) && message.getReporter() != radioGroup;
            }
        });
        feedbackPanel.setOutputMarkupPlaceholderTag(true);

        form.add(velgtemagruppeKnapp, new PlukkOppgaveKnapp("plukkOppgave"),
                radioGroup,
                new PlukkOppgaveKnapp("PlukkOppgaveFraTemaliste"),
                feedbackPanel,
                FeedbackLabel.create(radioGroup));

        add(form);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(forReference(new JavaScriptResourceReference(PlukkOppgavePanel.class, "plukkoppgave.js")));
    }

    private class PlukkOppgaveKnapp extends AjaxButton {
        public PlukkOppgaveKnapp(String id) {
            super(id);
            setMarkupId(id);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
            final Timer timer = createTimer("hendelse.plukk");
            timer.start();
            try {
                if (brukerHarEnAnnenPlukketOppgavePaaSession() && oppgavePaaSessionKanBehandles()) {
                    redirectForAaBesvareOppgave(
                            getSession().getAttribute(VALGT_OPPGAVE_FNR_ATTR),
                            getSession().getAttribute(VALGT_OPPGAVE_HENVENDELSEID_ATTR),
                            getSession().getAttribute(VALGT_OPPGAVE_ID_ATTR)
                    );
                    return;
                }

                Optional<Oppgave> oppgave = plukkOppgaveService.plukkOppgave(valgtTemagruppe.getObject(), saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());
                if (oppgave.isSome()) {
                    lagrePlukketOppgavePaaSession(oppgave.get());
                    lagreValgtTemagruppePaaSession(valgtTemagruppe.getObject());
                    redirectForAaBesvareOppgave(oppgave.get().fnr, oppgave.get().henvendelseId, oppgave.get().oppgaveId);
                } else {
                    error(getString("plukkoppgave.ingenoppgaverpaatemagruppe"));
                    target.prependJavaScript("fokusPlukkOppgaveTemagruppe();");
                    target.add(feedbackPanel, PlukkOppgavePanel.this.velgtemagruppeKnapp);
                    FeedbackLabel.addFormLabelsToTarget(target, submitForm);
                }
            } finally {
                timer.stop();
                timer.report();
            }
        }

        @Override
        protected void onError(AjaxRequestTarget target, Form<?> form) {
            target.add(feedbackPanel, PlukkOppgavePanel.this.velgtemagruppeKnapp);
            target.prependJavaScript("fokusPlukkOppgaveTemagruppe();");
            FeedbackLabel.addFormLabelsToTarget(target, form);
        }

        private void redirectForAaBesvareOppgave(Serializable fnr, Serializable henvendelseid, Serializable oppgaveid) {
            setResponsePage(PersonPage.class,
                    new PageParameters()
                            .set("fnr", fnr)
                            .set(HENVENDELSEID, henvendelseid)
                            .set(OPPGAVEID, oppgaveid)
                            .set(BESVARES, true)
            );
        }

        private boolean oppgavePaaSessionKanBehandles() {
            if (plukkOppgaveService.oppgaveErFerdigstilt((String) getSession().getAttribute(VALGT_OPPGAVE_ID_ATTR))) {
                getSession().setAttribute(VALGT_OPPGAVE_FNR_ATTR, null);
                getSession().setAttribute(VALGT_OPPGAVE_ID_ATTR, null);
                getSession().setAttribute(VALGT_OPPGAVE_HENVENDELSEID_ATTR, null);
                return false;
            }
            return true;
        }

        private boolean brukerHarEnAnnenPlukketOppgavePaaSession() {
            return getSession().getAttribute(VALGT_OPPGAVE_ID_ATTR) != null && getSession().getAttribute(VALGT_OPPGAVE_FNR_ATTR) != null;
        }

        private void lagrePlukketOppgavePaaSession(Oppgave oppgave) {
            getSession().setAttribute(VALGT_OPPGAVE_HENVENDELSEID_ATTR, oppgave.henvendelseId);
            getSession().setAttribute(VALGT_OPPGAVE_ID_ATTR, oppgave.oppgaveId);
            getSession().setAttribute(VALGT_OPPGAVE_FNR_ATTR, oppgave.fnr);
        }

        private void lagreValgtTemagruppePaaSession(Temagruppe temagruppe) {
            getSession().setAttribute(TEMAGRUPPE_ATTR, temagruppe);
        }

        @Override
        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
            attributes.setEventPropagation(EventPropagation.BUBBLE);
        }
    }
}
