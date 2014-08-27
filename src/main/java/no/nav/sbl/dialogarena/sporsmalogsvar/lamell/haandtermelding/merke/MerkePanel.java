package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.opprettoppgave.OpprettOppgavePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import javax.inject.Inject;

public class MerkePanel extends AnimertPanel {

    public static final String TRAAD_KONTORSPERRET = "sos.merkepanel.traadkontorsperret";

    @Inject
    private HenvendelseBehandlingService henvendelse;

    protected final OpprettOppgavePanel opprettOppgavePanel;

    public MerkePanel(String id, final InnboksVM innboksVM) {
        super(id);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        opprettOppgavePanel = new OpprettOppgavePanel("opprett-oppgave-panel", innboksVM);
        opprettOppgavePanel.setOutputMarkupId(true);
        add(opprettOppgavePanel);

        AjaxLink merkeLink = new AjaxLink("merk") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (opprettOppgavePanel.kanMerkeSomKontorsperret()) {
                    henvendelse.merkSomKontorsperret(innboksVM.getFnr(), innboksVM.getValgtTraad());
                    innboksVM.oppdaterMeldinger();
                    send(this, Broadcast.BUBBLE, TRAAD_KONTORSPERRET);
                    lukkPanel(target);
                } else {
                    error(getString("kontorsperre.oppgave.opprettet.feil"));
                    target.add(feedbackPanel);
                }
            }
        };
        add(merkeLink);

        AjaxLink<Void> avbrytLink = new AjaxLink<Void>("avbryt") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lukkPanel(target);
            }
        };
        add(avbrytLink);
    }

    @Override
    public void lukkPanel(AjaxRequestTarget target) {
        super.lukkPanel(target);
        opprettOppgavePanel.reset();
    }
}
