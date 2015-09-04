package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentCallback;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.HashMap;

public class ReactJournalforingsPanel extends AnimertPanel {

    public ReactJournalforingsPanel(String id, final InnboksVM innboksVM) {
        super(id, true);
        ReactComponentPanel reactComponentPanel = new ReactComponentPanel("reactjournalforing", "JournalforingsPanel", new HashMap<String, Object>() {
            {
                put("fnr", innboksVM.getFnr());
                put("traadId", innboksVM.getValgtTraad().getEldsteMelding().melding.traadId);
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

}
