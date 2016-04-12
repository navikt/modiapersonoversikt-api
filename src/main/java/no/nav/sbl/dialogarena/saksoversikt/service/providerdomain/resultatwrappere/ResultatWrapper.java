package no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem;

import java.util.HashSet;
import java.util.Set;

public class ResultatWrapper<T> {
    public T resultat;
    public Set<Baksystem> feilendeSystemer;

    public ResultatWrapper(T resultat) {
        this.resultat = resultat;
        feilendeSystemer = new HashSet<>();
    }

    public ResultatWrapper(T resultat, Set<Baksystem> feilendeSystemer) {
        this.resultat = resultat;
        this.feilendeSystemer = feilendeSystemer;
    }

    public ResultatWrapper<T> withEkstraFeilendeBaksystemer(Set<Baksystem> baksystemer){
        this.feilendeSystemer.addAll(baksystemer);
        return this;
    }
}
