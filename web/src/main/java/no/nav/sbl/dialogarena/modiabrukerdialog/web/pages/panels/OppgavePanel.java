package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.Modus;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave.HentOppgavePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg.OppgavevalgPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OppgavePanel extends Panel {

    private static final Logger LOG = LoggerFactory.getLogger(OppgavePanel.class);

    private final OppgavevalgPanel oppgavevalg;
    private final HentOppgavePanel hentOppgavePanel;

    public OppgavePanel(String id) {
        super(id);
        oppgavevalg = new OppgavevalgPanel("oppgavevalg");
        hentOppgavePanel = new HentOppgavePanel("hent-oppgave");
        initVisibility();
        add(hentOppgavePanel, oppgavevalg);
    }

    @RunOnEvents(Modus.BESVARE)
    public void besvarmodus(AjaxRequestTarget target, String oppgaveId) {
        LOG.info("Modus: {}. Oppgave: {}", Modus.BESVARE, oppgaveId);

        oppgavevalg.setVisibilityAllowed(true);
        hentOppgavePanel.setVisibilityAllowed(false);
        if (target != null) {
            target.add(oppgavevalg);
        }
    }

    @RunOnEvents(Modus.KVITTERING)
    public void kvitteringsmodus(AjaxRequestTarget target) {
        hentOppgavePanel.setVisibilityAllowed(true);
        if (target != null) {
            target.add(hentOppgavePanel);
        }
    }

    public final void initVisibility() {
        oppgavevalg.setVisibilityAllowed(false);
        hentOppgavePanel.setVisibilityAllowed(true);
    }
}
