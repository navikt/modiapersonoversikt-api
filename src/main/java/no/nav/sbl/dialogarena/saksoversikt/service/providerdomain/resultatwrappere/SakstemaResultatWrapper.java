package no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;

import java.util.List;
import java.util.Set;

public class SakstemaResultatWrapper {

    public List<Sakstema> sakstema;
    public Set<Baksystem> feilendeSystemer;

    public SakstemaResultatWrapper(List<Sakstema> sakstema, Set<Baksystem> feilendeSystemer) {
        this.sakstema = sakstema;
        this.feilendeSystemer = feilendeSystemer;
    }
}
