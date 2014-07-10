package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;

import java.io.Serializable;
import java.util.List;

public class JournalfortSakVM implements Serializable {

    private InnboksVM innboksVM;

    private MeldingService meldingService;

    private Sak journalfortSak;

    public JournalfortSakVM(InnboksVM innboksVM, MeldingService meldingService){
        this.innboksVM = innboksVM;
        this.meldingService = meldingService;
    }

    public void oppdater() {
        List<Sak> sakerForBruker = meldingService.hentSakerForBruker(innboksVM.getFnr());
        String journalfortSaksId = innboksVM.getValgtTraad().getEldsteMelding().melding.journalfortSaksId;
        boolean sakExists = false;
        for (Sak sak : sakerForBruker){
            if (sak.saksId.equals(journalfortSaksId)){
                journalfortSak = sak;
                sakExists = true;
            }
        }
        if (!sakExists)
            journalfortSak = new Sak();
    }

    public Sak getSak(){ return journalfortSak;}
}
