package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel;

import no.nav.metrics.MetricsFactory;
import no.nav.metrics.Timer;
import no.nav.modig.modia.feedbackform.FeedbackLabel;
import no.nav.modig.wicket.errorhandling.aria.AriaFeedbackPanel;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.DialogSession;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;
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
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.nav.brukerdialog.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.brukerdialog.security.tilgangskontroll.utils.AttributeUtils.resourceId;
import static no.nav.metrics.MetricsFactory.createTimer;
import static no.nav.modig.modia.security.WicketAutorizationUtils.accessRestriction;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SVAR_SKRIFTLIG;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage.FNR;
import static org.apache.wicket.ajax.attributes.AjaxRequestAttributes.EventPropagation;
import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;
import static org.slf4j.LoggerFactory.getLogger;

public class PlukkOppgavePanel extends Panel {

    public static final String TEMAGRUPPE_ATTR = "sos-temagruppe";

    @Inject
    private PlukkOppgaveService plukkOppgaveService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;

    private final IModel<Temagruppe> valgtTemagruppe;
    private final AriaFeedbackPanel feedbackPanel;
    private final Label velgtemagruppeKnapp;
    private DialogSession session;
    private Logger logger = getLogger(PlukkOppgavePanel.class);

    public PlukkOppgavePanel(String id) {
        super(id);

        session = DialogSession.read(this);
        add(accessRestriction(RENDER).withAttributes(actionId("plukkoppgave"), resourceId("")));

        valgtTemagruppe = new Model<>(DialogSession.read(this).getTemagruppe());
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

        form.add(velgtemagruppeKnapp, new PlukkOppgaveKnapp("plukkOppgaver"),
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
                session = DialogSession.read(this);
                fjernFerdigstilteOppgaverFraSession();
                if (!session.getPlukkedeOppgaver().isEmpty()) {
                    session.withOppgaveSomBesvares(session.getPlukkedeOppgaver().get(0))
                           .withOppgaverBlePlukket(true);
                    redirectForAaBesvareOppgave();
                    return;
                }

                List<Oppgave> oppgaver = plukkOppgaveService.plukkOppgaver(valgtTemagruppe.getObject(), saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());
                if (!oppgaver.isEmpty()) {
                    MetricsFactory.createEvent("hendelse.plukk.oppgaver-tildelt")
                            .addFieldToReport("antall", oppgaver.size())
                            .report();
                    opprettSvarHenvendelserForOppgaver(oppgaver);
                    session.withPlukkedeOppgaver(oppgaver)
                           .withValgtTemagruppe(valgtTemagruppe.getObject())
                           .withOppgaveSomBesvares(oppgaver.get(0));
                    redirectForAaBesvareOppgave();
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

        private void opprettSvarHenvendelserForOppgaver(List<Oppgave> oppgaver) {
            for (Oppgave oppgave : oppgaver) {
                oppgave.withSvarHenvendelseId(
                    henvendelseUtsendingService.opprettHenvendelse(
                            SVAR_SKRIFTLIG.toString(),
                            oppgave.fnr,
                            oppgave.henvendelseId
                    )
                );
            }
        }

        @Override
        protected void onError(AjaxRequestTarget target, Form<?> form) {
            target.add(feedbackPanel, PlukkOppgavePanel.this.velgtemagruppeKnapp);
            target.prependJavaScript("fokusPlukkOppgaveTemagruppe();");
            FeedbackLabel.addFormLabelsToTarget(target, form);
        }

        private void redirectForAaBesvareOppgave() {
            setResponsePage(PersonPage.class, new PageParameters().set(FNR, session.getOppgaveSomBesvares().get().fnr));
        }

        private void fjernFerdigstilteOppgaverFraSession() {
            session.withPlukkedeOppgaver(
                    session.getPlukkedeOppgaver().stream()
                            .filter(oppgave -> !plukkOppgaveService.oppgaveErFerdigstilt(oppgave.oppgaveId))
                            .collect(toList())
            );
        }

        private boolean brukerHarEnAnnenPlukketOppgavePaaSession() {
            return !session.getPlukkedeOppgaver().isEmpty();
        }

        @Override
        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
            attributes.setEventPropagation(EventPropagation.BUBBLE);
        }
    }
}
