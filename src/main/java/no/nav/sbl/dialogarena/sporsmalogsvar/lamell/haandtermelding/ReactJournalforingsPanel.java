package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;

public class ReactJournalforingsPanel extends AnimertPanel {
    public ReactJournalforingsPanel(String id) {
        super(id, true);
        add(new ReactComponentPanel("reactjournalforing", "JournalforingsPanel"));
    }
}
