package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave.OppgavePanel;
import org.apache.wicket.markup.html.panel.Panel;

public class MeldingActionPanel extends Panel {

    public final ReactJournalforingsPanel journalforPanel;
    public final OppgavePanel oppgavePanel;
    public final MerkePanel merkePanel;

    public MeldingActionPanel(String id, InnboksVM innboksVM) {
        super(id);
        journalforPanel = new ReactJournalforingsPanel("journalforPanel", innboksVM);
        oppgavePanel = new OppgavePanel("nyoppgavePanel", innboksVM);
        merkePanel = new MerkePanel("merkePanel", innboksVM);

        add(journalforPanel, oppgavePanel, merkePanel);
    }
}
