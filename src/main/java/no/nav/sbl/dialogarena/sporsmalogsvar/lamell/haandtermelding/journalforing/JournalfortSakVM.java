package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;

import java.io.Serializable;
import java.util.List;

public class JournalfortSakVM implements Serializable {

    private Sak sak;

    private InnboksVM innboksVM;
    private GsakService gsakService;

    public JournalfortSakVM(InnboksVM innboksVM, GsakService gsakService) {
        this.innboksVM = innboksVM;
        this.gsakService = gsakService;
    }

    public final void oppdater() {
        List<Sak> sakerForBruker = gsakService.hentSakerForBruker(innboksVM.getFnr());
        String journalfortSaksId = innboksVM.getValgtTraad().getEldsteMelding().melding.journalfortSaksId;

        sak = finnJournalfortSakHvisDenEksisterer(sakerForBruker, journalfortSaksId);
    }

    private Sak finnJournalfortSakHvisDenEksisterer(List<Sak> sakerForBruker, String journalfortSaksId) {
        for (Sak sakForBruker : sakerForBruker) {
            if (sakForBruker.saksId.equals(journalfortSaksId)) {
                return sakForBruker;
            }
        }
        return new Sak();
    }

    public Sak getSak() {
        return sak;
    }

}
