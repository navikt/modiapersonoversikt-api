package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.list.UnmodifiableList;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class SakerForTema implements Serializable, Comparable<SakerForTema> {
    public String temaKode;
    public String temaNavn;

    public List<Sak> saksliste;

    public SakerForTema() {
    }

    public SakerForTema(String temaKode, String temaNavn, List<Sak> saksliste) {
        this.temaKode = temaKode;
        this.temaNavn = temaNavn;
        this.saksliste = saksliste;
    }

    public SakerForTema withTemaKode(String temaKode){
        this.temaKode = temaKode;
        return this;
    }

    public SakerForTema withTemaNavn(String temaNavn){
        this.temaNavn = temaNavn;
        return this;
    }

    public SakerForTema withSaksliste(List<Sak> saksliste){
        Collections.sort(saksliste);
        this.saksliste = UnmodifiableList.decorate(saksliste);
        return this;
    }

    public static final Transformer<SakerForTema, String> TEMA_KODE = new Transformer<SakerForTema, String>() {
        @Override
        public String transform(SakerForTema temasaker) {
            return temasaker.temaKode;
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
                && !(temaKode != null ? !temaKode.equals(sakerForTema.temaKode) : sakerForTema.temaKode != null);

    }

    @Override
    public int hashCode() {
        int result = temaKode != null ? temaKode.hashCode() : 0;
        result = 31 * result + (saksliste != null ? saksliste.hashCode() : 0);
        return result;
    }
}


