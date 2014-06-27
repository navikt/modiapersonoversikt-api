package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sakstema;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SakerVM implements Serializable {

    private InnboksVM innboksVM;

    private List<Sakstema> sakstemaliste;

    private MeldingService meldingService;

    public SakerVM(InnboksVM innboksVM, MeldingService meldingService) {
        this.innboksVM = innboksVM;
        this.meldingService = meldingService;
        oppdater();
    }

    public void oppdater() {
        sakstemaliste = mapSakerTilSakstema(meldingService.hentSakerForBruker(innboksVM.getFnr()));
    }

    private List<Sakstema> mapSakerTilSakstema(List<Sak> saksListe) {
        List<Sakstema> sakstemaListe = new ArrayList<>();
        boolean eksisterendeSaksTema = false;
        for (Sak sak : saksListe) {
            String tema = sak.tema;
            for (Sakstema sakstema : sakstemaListe) {
                if (sakstema.tema.equals(tema)) {
                    sakstema.saksliste.add(sak);
                    eksisterendeSaksTema = true;
                }
            }
            if (!eksisterendeSaksTema) {
                Sakstema sakstema = new Sakstema(sak.tema);
                sakstema.saksliste.add(sak);
                sakstemaListe.add(sakstema);
            }
        }

        return sakstemaListe;
    }


    public List<Sakstema> getSakstemaliste() {
        return sakstemaliste;
    }

}
