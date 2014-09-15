package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class Sak implements Serializable, Comparable<Sak> {

    public String saksId, tema, fagsystem, sakstype;
    public DateTime opprettetDato;

    public static final String SAKSTYPE_GENERELL = "GEN";
    public static final List<String> GODKJENTE_TEMA_FOR_GENERELLE = unmodifiableList(asList("AGR", "FUL", "GEN", "KTR", "STO", "SER", "SIK", "SYM", "TRK", "TRY", "VEN"));
    public static final List<String> GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER = unmodifiableList(asList("AO01", "IT01", "OEBS", "V2", "AO11"));
    public static final String GODKJENT_FAGSYSTEM_FOR_GENERELLE = "FS22";

    public boolean isSakstypeForVisningGenerell() {
        return SAKSTYPE_GENERELL.equals(sakstype);
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
            return sak.isSakstypeForVisningGenerell();
        }
    };

    public static final Predicate<Sak> IS_GODKJENT_FAGSYSTEM_FOR_FAGSAK = new Predicate<Sak>() {
        @Override
        public boolean evaluate(Sak sak) {
            return GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.contains(sak.fagsystem);
        }
    };

    public static final Predicate<Sak> IS_GODKJENT_FAGSYSTEM_FOR_GENERELLE = new Predicate<Sak>() {
        @Override
        public boolean evaluate(Sak sak) {
            return GODKJENT_FAGSYSTEM_FOR_GENERELLE.equals(sak.fagsystem);
        }
    };

    public static final Predicate<Sak> IS_GODKJENT_TEMA_FOR_GENERELLE = new Predicate<Sak>() {
        @Override
        public boolean evaluate(Sak sak) {
            return GODKJENTE_TEMA_FOR_GENERELLE.contains(sak.tema);
        }
    };

    public String getOpprettetDatoFormatert() {
        return Datoformat.kortMedTid(opprettetDato);
    }

    @Override
    public int compareTo(Sak other) {
        return other.opprettetDato.compareTo(opprettetDato);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Sak sak = (Sak) o;

        return !(saksId != null ? !saksId.equals(sak.saksId) : sak.saksId != null);
    }

    @Override
    public int hashCode() {
        return saksId != null ? saksId.hashCode() : 0;
    }

}
