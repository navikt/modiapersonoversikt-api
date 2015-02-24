package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain;

import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class Sak implements Serializable, Comparable<Sak> {

    public String saksId, temaKode, temaNavn, fagsystemKode, fagsystemNavn, sakstype;
    public DateTime opprettetDato;
    public Boolean finnesIGsak;

    public static final String TEMAKODE_OPPFOLGING = "OPP";
    public static final String SAKSTYPE_GENERELL = "GEN";
    public static final List<String> GODKJENTE_TEMA_FOR_GENERELLE = unmodifiableList(asList("AGR", "FUL", "GEN", "KTR", "STO", "SER", "SIK", "SYM", "TRK", "TRY", "VEN"));
    public static final List<String> GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER = unmodifiableList(asList("AO01", "IT01", "OEBS", "V2", "AO11"));
    public static final String GODKJENT_FAGSYSTEM_FOR_GENERELLE = "FS22";

    public boolean isSakstypeForVisningGenerell() {
        return SAKSTYPE_GENERELL.equals(sakstype);
    }

    public static final Transformer<Sak, String> TEMAKODE = new Transformer<Sak, String>() {
        @Override
        public String transform(Sak sak) {
            return sak.temaKode;
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
            return GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.contains(sak.fagsystemKode);
        }
    };

    public static final Predicate<Sak> IS_GODKJENT_FAGSYSTEM_FOR_GENERELLE = new Predicate<Sak>() {
        @Override
        public boolean evaluate(Sak sak) {
            return GODKJENT_FAGSYSTEM_FOR_GENERELLE.equals(sak.fagsystemKode);
        }
    };

    public static final Predicate<Sak> IS_GODKJENT_TEMA_FOR_GENERELLE = new Predicate<Sak>() {
        @Override
        public boolean evaluate(Sak sak) {
            return GODKJENTE_TEMA_FOR_GENERELLE.contains(sak.temaKode);
        }
    };

    public static final Predicate<Sak> IS_ARENA_OPPFOLGING = new Predicate<Sak>() {
        @Override
        public boolean evaluate(Sak sak) {
            return TEMAKODE_OPPFOLGING.equals(sak.temaKode)
                    && "AO01".equals(sak.fagsystemKode)
                    && "MFS".equals(sak.sakstype);
        }
    };

    public String getOpprettetDatoFormatert() { return Datoformat.langUtenLiteral(opprettetDato); }

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
