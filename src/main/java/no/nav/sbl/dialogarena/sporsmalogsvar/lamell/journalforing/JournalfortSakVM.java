package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;

import java.io.Serializable;
import java.util.List;

public class JournalfortSakVM implements Serializable {

    private InnboksVM innboksVM;

    private MeldingService meldingService;

    public JournalfortSakVM(InnboksVM innboksVM, MeldingService meldingService){
        this.innboksVM = innboksVM;
        this.meldingService = meldingService;

    }

    public Sak getSak(){
        List<Sak> sakerForBruker = meldingService.hentSakerForBruker(innboksVM.getFnr());
        String journalfortSaksId = innboksVM.getValgtTraad().getEldsteMelding().melding.journalfortSaksId;
        for (Sak sak : sakerForBruker){
            if (sak.saksId.equals(journalfortSaksId)){
                return(sak);
            }
        }
        return new Sak();
    }
}
