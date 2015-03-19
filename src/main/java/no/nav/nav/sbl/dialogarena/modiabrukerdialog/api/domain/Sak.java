package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static no.nav.modig.lang.option.Optional.none;

public class Sak implements Serializable, Comparable<Sak> {

    public Optional<String> saksId = none();
    public Optional<String> fagsystemSaksId = none();
    public String temaKode, temaNavn, fagsystemKode, fagsystemNavn, sakstype;
    public DateTime opprettetDato;
    public Boolean finnesIGsak;

    public static final String TEMAKODE_OPPFOLGING = "OPP";
    public static final String SAKSTYPE_GENERELL = "GEN";
    public static final String SAKSTYPE_MED_FAGSAK = "MFS";
    public static final String FAGSYSTEMKODE_ARENA = "AO01";

    public static final String GODKJENT_FAGSYSTEM_FOR_GENERELLE = "FS22";
    public static final List<String> GODKJENTE_TEMA_FOR_GENERELLE = unmodifiableList(asList("AGR", "FUL", "GEN", "KTR", "STO", "SER", "SYM", "TRK", "TRY", "VEN", "UFM", TEMAKODE_OPPFOLGING));
    public static final List<String> GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER = unmodifiableList(asList(FAGSYSTEMKODE_ARENA, "IT01", "OEBS", "V2", "AO11"));

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

    public static final Predicate<Sak> IS_GODKJENT_FAGSYSTEM_FOR_FAGSAK = new Predicate<Sak>() {
        @Override
        public boolean evaluate(Sak sak) {
            return GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.contains(sak.fagsystemKode);
        }
    };

    public static final Predicate<Sak> IS_ARENA_OPPFOLGING = new Predicate<Sak>() {
        @Override
        public boolean evaluate(Sak sak) {
            return TEMAKODE_OPPFOLGING.equals(sak.temaKode)
                    && FAGSYSTEMKODE_ARENA.equals(sak.fagsystemKode)
                    && SAKSTYPE_MED_FAGSAK.equals(sak.sakstype);
        }
    };

    public String getOpprettetDatoFormatert() {
        return Datoformat.langUtenLiteral(opprettetDato);
    }

    public String getSaksIdVisning() {
        if (fagsystemSaksId.isSome()) {
            return fagsystemSaksId.get();
        } else if (saksId.isSome()) {
            return saksId.get();
        } else {
            return "";
        }
    }

    @Override
    public int compareTo(Sak other) {
        return other.opprettetDato.compareTo(opprettetDato);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Sak sak = (Sak) obj;
        if (saksId.isSome() && sak.saksId.isSome() && saksId.get().equals(sak.saksId.get())) {
            return true;
        } else {
            return temaKode != null && sak.temaKode != null
                    && fagsystemKode != null && sak.fagsystemKode != null
                    && sakstype != null && sak.sakstype != null
                    && temaKode.equals(sak.temaKode)
                    && fagsystemKode.equals(sak.fagsystemKode)
                    && sakstype.equals(sak.sakstype);
        }
    }

    @Override
    public int hashCode() {
        if (saksId.isSome()) {
            return saksId.get().hashCode();
        } else if (temaKode != null
                && fagsystemKode != null
                && sakstype != null) {
            return temaKode.hashCode() * fagsystemKode.hashCode() * sakstype.hashCode();
        } else {
            return 0;
        }
    }

}
