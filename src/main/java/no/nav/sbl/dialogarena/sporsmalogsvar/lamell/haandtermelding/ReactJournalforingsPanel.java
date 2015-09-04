package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentCallback;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.HashMap;

public class ReactJournalforingsPanel extends AnimertPanel {

    private final ReactComponentPanel reactComponentPanel;
    private final InnboksVM innboksVM;

    public ReactJournalforingsPanel(String id, final InnboksVM innboksVM) {
        super(id, true);
        this.innboksVM = innboksVM;
        reactComponentPanel = new ReactComponentPanel("reactjournalforing", "JournalforingsPanel", new HashMap<String, Object>() {
            {
                put("fnr", innboksVM.getFnr());
            }
        });
        reactComponentPanel.addCallback("lukkPanel", Void.class, new ReactComponentCallback<Void>() {
            @Override
            public void onCallback(AjaxRequestTarget target, Void data) {
                lukkPanel(target);
            }
        });
        add(reactComponentPanel);
    }

    @Override
    protected void onOpen() {
        reactComponentPanel.updateState(new HashMap<String, Object>() {
            {
                put("traadId", innboksVM.getValgtTraad().getEldsteMelding().melding.traadId);
            }
        });
    }
}
