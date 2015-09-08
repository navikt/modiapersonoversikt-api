package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.TemagruppeTemaMapping;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentCallback;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;

import java.util.HashMap;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.TemagruppeTemaMapping.TEMAGRUPPE_TEMA_MAPPING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.AnimertJournalforingsPanel.TRAAD_JOURNALFORT;

public class ReactJournalforingsPanel extends AnimertPanel {

    private final ReactComponentPanel reactComponentPanel;
    private final InnboksVM innboksVM;

    public ReactJournalforingsPanel(String id, final InnboksVM innboksVM) {
        super(id, true);
        this.innboksVM = innboksVM;
        reactComponentPanel = new ReactComponentPanel("reactjournalforing", "JournalforingsPanel", new HashMap<String, Object>() {
            {
                put("fnr", innboksVM.getFnr());
                put("temagruppeTemaMapping", TEMAGRUPPE_TEMA_MAPPING);
            }
        });
        reactComponentPanel.addCallback("lukkPanel", Void.class, new ReactComponentCallback<Void>() {
            @Override
            public void onCallback(AjaxRequestTarget target, Void data) {
                lukkPanel(target);
            }
        });
        reactComponentPanel.addCallback("traadJournalfort", Void.class, new ReactComponentCallback<Void>() {
            @Override
            public void onCallback(AjaxRequestTarget target, Void data) {
                lukkPanel(target);
                send(getPage(), Broadcast.DEPTH, TRAAD_JOURNALFORT);
            }
        });
        add(reactComponentPanel);
    }

    @Override
    protected void onOpen() {
        reactComponentPanel.updateState(new HashMap<String, Object>() {
            {
                put("traadId", innboksVM.getValgtTraad().getEldsteMelding().melding.traadId);
                put("temagruppe", innboksVM.getValgtTraad().getEldsteMelding().melding.temagruppe);
            }
        });
    }
}
