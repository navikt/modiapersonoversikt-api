package no.nav.sbl.dialogarena.sporsmalogsvar.common.common.journalfor.domene;

import no.nav.sbl.dialogarena.sporsmalogsvar.common.Traad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static no.nav.modig.lang.collections.IterUtils.by;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;

public class Journalforing implements Serializable {

    public Sak valgtSak;
    private final SortedMap<String, List<Sak>> sakerPerTema;
    public final Traad traad;

    public Journalforing(Traad traad, Iterable<Sak> saker) {
        this.traad = traad;
        sakerPerTema = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (String temakode : on(saker).map(Sak.TEMAKODE)) {
            sakerPerTema.put(temakode, on(saker).filter(where(Sak.TEMAKODE, equalTo(temakode))).collect(by(Sak.OPPRETTET_DATO).descending()));
        }
    }

    public List<String> getTemakoder() {
        return new ArrayList<>(sakerPerTema.keySet());
    }

    public boolean harSaker() {
        return !sakerPerTema.isEmpty();
    }

    public List<Sak> getSaker(String temakode) {
        return sakerPerTema.get(temakode);
    }

    public boolean isSensitiv() {
        return traad.erSensitiv;
    }

    public void setSensitiv(boolean sensitiv) {
        traad.erSensitiv = sensitiv;
    }
}
