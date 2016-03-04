package no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj;

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
