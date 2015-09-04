package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;

import java.util.HashMap;

public class ReactJournalforingsPanel extends AnimertPanel {

    private final ReactComponentPanel reactComponentPanel;

    public ReactJournalforingsPanel(String id, final InnboksVM innboksVM) {
        super(id, true);
        reactComponentPanel = new ReactComponentPanel("reactjournalforing", "JournalforingsPanel", new HashMap<String, Object>() {
            {
                put("fnr", innboksVM.getFnr());
                put("traadId", innboksVM.getValgtTraad().getEldsteMelding().melding.traadId);
            }
        });
        add(reactComponentPanel);
    }

    @Override
    protected void onOpen() {
        super.onOpen();
    }
}
