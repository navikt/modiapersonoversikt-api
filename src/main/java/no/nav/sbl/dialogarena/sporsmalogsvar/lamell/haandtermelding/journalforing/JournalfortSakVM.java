package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;

import java.io.Serializable;
import java.util.List;

public class JournalfortSakVM implements Serializable {

    private Sak sak;
    private InnboksVM innboksVM;
    private SakerService sakerService;

    public JournalfortSakVM(InnboksVM innboksVM, SakerService sakerService) {
        this.innboksVM = innboksVM;
        this.sakerService = sakerService;
    }

    public final void oppdater() {
        List<Sak> sakerForBruker = sakerService.hentListeAvSaker(innboksVM.getFnr());
        String journalfortSaksId = innboksVM.getValgtTraad().getEldsteMelding().melding.journalfortSaksId;

        sak = finnJournalfortSakHvisDenEksisterer(sakerForBruker, journalfortSaksId);
    }

    private Sak finnJournalfortSakHvisDenEksisterer(List<Sak> sakerForBruker, String journalfortSaksId) {
        for (Sak sakForBruker : sakerForBruker) {
            if (sakForBruker.saksId.isSome() && sakForBruker.saksId.get().equals(journalfortSaksId)) {
                return sakForBruker;
            }
        }
        return new Sak();
    }

    public Sak getSak() {
        return sak;
    }

}
