package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.list.UnmodifiableList;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class SakerForTema implements Serializable, Comparable<SakerForTema> {
    public String temagruppe;
    public String temaKode;
    public String temaNavn;

    public List<Sak> saksliste;

    public SakerForTema(String temaKode, String temaNavn, String temagruppe, List<Sak> saksliste) {
        this.temaKode = temaKode;
        this.temagruppe = temagruppe;
        this.temaNavn = temaNavn;

        Collections.sort(saksliste);
        this.saksliste = UnmodifiableList.decorate(saksliste);
    }

    public static final Transformer<SakerForTema, String> TEMAGRUPPE = new Transformer<SakerForTema, String>() {
        @Override
        public String transform(SakerForTema temasaker) {
            return temasaker.temagruppe;
        }
    };

    @Override
    public int compareTo(SakerForTema other) {
        return temaNavn.compareTo(other.temaNavn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SakerForTema sakerForTema = (SakerForTema) o;

        return !(saksliste != null ? !saksliste.equals(sakerForTema.saksliste) : sakerForTema.saksliste != null)
                && !(temaKode != null ? !temaKode.equals(sakerForTema.temaKode) : sakerForTema.temaKode != null)
                && !(temagruppe != null ? !temagruppe.equals(sakerForTema.temagruppe) : sakerForTema.temagruppe != null);

    }

    @Override
    public int hashCode() {
        int result = temaKode != null ? temaKode.hashCode() : 0;
        result = 31 * result + (temagruppe != null ? temagruppe.hashCode() : 0);
        result = 31 * result + (saksliste != null ? saksliste.hashCode() : 0);
        return result;
    }
}


