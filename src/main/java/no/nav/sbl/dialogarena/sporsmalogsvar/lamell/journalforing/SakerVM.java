package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;

import java.io.Serializable;
import java.util.List;

public class SakerVM implements Serializable {

    private InnboksVM innboksVM;

    private List<Sak> saksliste;

    public SakerVM(InnboksVM innboksVM, MeldingService meldingService) {
        this.innboksVM = innboksVM;
        saksliste = meldingService.hentSakerForBruker(innboksVM.getFnr());
    }

    public List<Sak> getSaksliste() {
        return saksliste;
    }
}
