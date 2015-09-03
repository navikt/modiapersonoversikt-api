package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Saker;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

public class ReactJournalforingsPanel extends AnimertPanel {

    private final ReactComponentPanel reactComponentPanel;
    private final String fnr;
    @Inject
    private SakerService sakerService;

    public ReactJournalforingsPanel(String id, InnboksVM innboksVM) {
        super(id, true);
        fnr = innboksVM.getFnr();
        final List<Sak> saker = sakerService.hentRelevanteSaker(fnr);
        reactComponentPanel = new ReactComponentPanel("reactjournalforing", "JournalforingsPanel", new HashMap<String, Object>() {
            {
                put("saker", saker);
            }
        });
        add(reactComponentPanel);
    }

    @Override
    protected void onOpen() {
        super.onOpen();
    }
}
