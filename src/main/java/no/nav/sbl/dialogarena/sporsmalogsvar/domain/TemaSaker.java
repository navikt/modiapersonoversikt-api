package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import org.apache.commons.collections15.Transformer;

import java.io.Serializable;
import java.util.List;

public class TemaSaker implements Serializable, Comparable<TemaSaker> {
    public String tema;

    public String temagruppe;

    public List<Sak> saksliste;

    public TemaSaker(String tema, String temagruppe, List<Sak> saksliste) {
        this.tema = tema;
        this.temagruppe = temagruppe;
        this.saksliste = saksliste;
    }

    public static final Transformer<TemaSaker, String> TEMAGRUPPE = new Transformer<TemaSaker, String>() {
        @Override
        public String transform(TemaSaker temasaker) {
            return temasaker.temagruppe;
        }
    };

    @Override
    public int compareTo(TemaSaker other) {
        return tema.compareTo(other.tema);
    }
}


