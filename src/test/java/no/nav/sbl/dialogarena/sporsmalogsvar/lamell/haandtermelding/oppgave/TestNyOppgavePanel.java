package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;

public class TestNyOppgavePanel extends NyOppgavePanel {
    public TestNyOppgavePanel(String id, InnboksVM innboksVM) {
        super(id, innboksVM);
        setVisibilityAllowed(true);
    }
}
