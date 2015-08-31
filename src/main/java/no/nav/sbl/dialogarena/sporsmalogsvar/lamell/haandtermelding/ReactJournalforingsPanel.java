package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

public class ReactJournalforingsPanel extends AnimertPanel {

    @Inject
    private SakerService sakerService;

    public ReactJournalforingsPanel(String id, InnboksVM innboksVM) {
        super(id, true);
        final List<Sak> saker = sakerService.hentListeAvSaker(innboksVM.getFnr());
        add(new ReactComponentPanel("reactjournalforing", "JournalforingsPanel", new HashMap<String, Object>() {
            {
                put("saker", saker);
            }
        }));
    }
}
