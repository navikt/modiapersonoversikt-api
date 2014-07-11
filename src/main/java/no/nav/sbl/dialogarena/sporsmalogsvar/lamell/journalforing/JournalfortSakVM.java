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

    public JournalfortSakVM(InnboksVM innboksVM, MeldingService meldingService){
        this.innboksVM = innboksVM;
        this.meldingService = meldingService;
        oppdater();
    }

    public void oppdater() {
        List<Sak> sakerForBruker = meldingService.hentSakerForBruker(innboksVM.getFnr());
        String journalfortSaksId = innboksVM.getValgtTraad().getEldsteMelding().melding.journalfortSaksId;
        sak = new Sak();
        for (Sak sak : sakerForBruker){
            if (sak.saksId.equals(journalfortSaksId)){
                this.sak = sak;
            }
        }
    }
    public Sak getSak(){ return sak;}
}
