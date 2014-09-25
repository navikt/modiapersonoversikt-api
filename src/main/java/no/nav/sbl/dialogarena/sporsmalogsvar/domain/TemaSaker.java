package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import org.apache.commons.collections15.Transformer;

import java.io.Serializable;
import java.util.List;

public class TemaSaker implements Serializable, Comparable<TemaSaker> {
    public String temagruppe;
    public String temaKode;
    public String temaNavn;

    public List<Sak> saksliste;

    public TemaSaker(String temaKode, String temaNavn, String temagruppe, List<Sak> saksliste) {
        this.temaKode = temaKode;
        this.temagruppe = temagruppe;
        this.temaNavn = temaNavn;
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
        return temaKode.compareTo(other.temaKode);
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
                && !(temaKode != null ? !temaKode.equals(temaSaker.temaKode) : temaSaker.temaKode != null)
                && !(temagruppe != null ? !temagruppe.equals(temaSaker.temagruppe) : temaSaker.temagruppe != null);

    }

    @Override
    public int hashCode() {
        int result = temaKode != null ? temaKode.hashCode() : 0;
        result = 31 * result + (temagruppe != null ? temagruppe.hashCode() : 0);
        result = 31 * result + (saksliste != null ? saksliste.hashCode() : 0);
        return result;
    }
}


