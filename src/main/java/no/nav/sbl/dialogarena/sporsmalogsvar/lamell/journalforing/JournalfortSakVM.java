package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;

import java.io.Serializable;
import java.util.List;

public class JournalfortSakVM implements Serializable {

    private InnboksVM innboksVM;

    private MeldingService meldingService;

    private Sak sak;

    public JournalfortSakVM(InnboksVM innboksVM, MeldingService meldingService) {
        this.innboksVM = innboksVM;
        this.meldingService = meldingService;
    }

    public final void oppdater() {
        List<Sak> sakerForBruker = meldingService.hentSakerForBruker(innboksVM.getFnr());
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
