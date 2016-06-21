package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;

import java.util.HashMap;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.TemagruppeTemaMapping.TEMAGRUPPE_TEMA_MAPPING;

public class ReactJournalforingsPanel extends AnimertPanel {

    public static final String TRAAD_JOURNALFORT = "sos.journalforingspanel.traadJournalfort";

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
        reactComponentPanel.addCallback("lukkPanel", Void.class, (target, data) -> lukkPanel(target));
        reactComponentPanel.addCallback("traadJournalfort", Void.class, (target, data) -> {
            lukkPanel(target);
            send(getPage(), Broadcast.DEPTH, TRAAD_JOURNALFORT);
        });
        add(reactComponentPanel);
    }

    @SuppressWarnings("unused")
    @RunOnEvents(Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER)
    public void lukkJournalforingsPanel(AjaxRequestTarget target) {
        lukkPanel(target);
    }

    @Override
    protected void onOpen() {
        reactComponentPanel.updateState(new HashMap<String, Object>() {
            {
                put("traadId", innboksVM.getValgtTraad().getEldsteMelding().melding.traadId);
                put("temagruppe", innboksVM.getValgtTraad().getEldsteMelding().melding.gjeldendeTemagruppe);
            }
        });
    }
}
