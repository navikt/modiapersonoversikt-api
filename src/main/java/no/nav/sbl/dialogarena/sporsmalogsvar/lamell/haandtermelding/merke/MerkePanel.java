package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.opprettoppgave.OpprettOppgave;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import javax.inject.Inject;

public class MerkePanel extends AnimertPanel {

    public static final String TRAAD_KONTORSPERRET = "sos.merkepanel.traadkontorsperret";

    @Inject
    private HenvendelseBehandlingService henvendelse;

    public MerkePanel(String id, final InnboksVM innboksVM) {
        super(id);


        final OpprettOppgave opprettOppgavePanel = new OpprettOppgave("opprett-oppgave-panel", innboksVM);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);

        AjaxLink merkeLink = new AjaxLink("merk") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (opprettOppgavePanel.kanMerkeSomKontorsperret()) {
                    henvendelse.merkSomKontorsperret(innboksVM.getFnr(), innboksVM.getValgtTraad());
                    innboksVM.oppdaterMeldinger();
                    lukkPanel(target);

                    opprettOppgavePanel.reset();
                } else {
                    error(getString("kontorsperre.oppgave.opprettet.feil"));
                    target.add(feedbackPanel);
                }
            }
        };

        AjaxLink<Void> avbrytLink = new AjaxLink<Void>("avbryt") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lukkPanel(target);
            }
        };
        add(opprettOppgavePanel, feedbackPanel, merkeLink, avbrytLink);
    }
}
