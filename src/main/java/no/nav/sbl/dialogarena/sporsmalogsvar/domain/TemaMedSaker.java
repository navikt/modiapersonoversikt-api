package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class TemaMedSaker implements Serializable{
    public String tema;

    public String temagruppe;

    public List<Sak> saksliste;

    public TemaMedSaker(String tema, String temagruppe, List<Sak> saksliste) {
        this.tema = tema;
        this.temagruppe = temagruppe;
        this.saksliste = saksliste;
    }

    public static final Comparator<TemaMedSaker> SAMMENLIGN_TEMA = new Comparator<TemaMedSaker>() {
        @Override
        public int compare(TemaMedSaker o1, TemaMedSaker o2) {
            return o2.tema.compareTo(o1.tema);
        }
    };

}


