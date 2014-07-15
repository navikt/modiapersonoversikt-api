package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;

public class Sak implements Serializable, Comparable<Sak> {

    public String saksId, tema, fagsystem, sakstype;
    public DateTime opprettetDato;

    public static final String SAKSTYPE_GENERELL = "Generell";
    public static final String SAKSTEMA_OPPFOLGING = "Oppfølging";

    public boolean isSakstypeForVisingGenerell(){
        return sakstype.equals(SAKSTYPE_GENERELL) && (!tema.equals(SAKSTEMA_OPPFOLGING));
    }

    public static final Transformer<Sak, String> TEMA = new Transformer<Sak, String>() {
        @Override
        public String transform(Sak sak) {
            return sak.tema;
        }
    };

    public static final Transformer<Sak, Boolean> IS_GENERELL_SAK = new Transformer<Sak, Boolean>() {
        @Override
        public Boolean transform(Sak sak) {
            return sak.isSakstypeForVisingGenerell();
        }
    };

    public String getOpprettetDatoFormatert(){
        return Datoformat.kortMedTid(opprettetDato);
    }

    @Override
    public int compareTo(Sak other) {
        return other.opprettetDato.compareTo(opprettetDato);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Sak sak = (Sak) o;

        return !(saksId != null ? !saksId.equals(sak.saksId) : sak.saksId != null);
    }

    @Override
    public int hashCode() {
        return saksId != null ? saksId.hashCode() : 0;
    }
}
