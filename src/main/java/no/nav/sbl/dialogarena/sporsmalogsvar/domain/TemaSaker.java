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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TemaSaker temaSaker = (TemaSaker) o;

        return !(saksliste != null ? !saksliste.equals(temaSaker.saksliste) : temaSaker.saksliste != null)
                && !(tema != null ? !tema.equals(temaSaker.tema) : temaSaker.tema != null)
                && !(temagruppe != null ? !temagruppe.equals(temaSaker.temagruppe) : temaSaker.temagruppe != null);

    }

    @Override
    public int hashCode() {
        int result = tema != null ? tema.hashCode() : 0;
        result = 31 * result + (temagruppe != null ? temagruppe.hashCode() : 0);
        result = 31 * result + (saksliste != null ? saksliste.hashCode() : 0);
        return result;
    }
}


