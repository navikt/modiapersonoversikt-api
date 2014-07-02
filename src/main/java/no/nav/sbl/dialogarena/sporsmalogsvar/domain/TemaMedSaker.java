package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import java.io.Serializable;
import java.util.List;

public class TemaMedSaker implements Serializable, Comparable<TemaMedSaker> {
    public String tema;

    public String temagruppe;

    public List<Sak> saksliste;

    public TemaMedSaker(String tema, String temagruppe, List<Sak> saksliste) {
        this.tema = tema;
        this.temagruppe = temagruppe;
        this.saksliste = saksliste;
    }

    @Override
    public int compareTo(TemaMedSaker other) {
        return tema.compareTo(other.tema);
    }
}


