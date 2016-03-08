package no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sak;

import java.util.List;
import java.util.Set;

public class AlleSakerResultatWrapper {

    public List<Sak> alleSaker;
    public Set<Baksystem> feilendeSystemer;

    public AlleSakerResultatWrapper(List<Sak> alleSaker, Set<Baksystem> feilendeSystemer) {
        this.alleSaker = alleSaker;
        this.feilendeSystemer = feilendeSystemer;
    }

}
