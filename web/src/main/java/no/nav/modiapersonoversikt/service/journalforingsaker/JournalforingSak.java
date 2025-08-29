package no.nav.modiapersonoversikt.service.journalforingsaker;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JournalforingSak implements Serializable, Comparable<JournalforingSak> {
    public String fnr = null;
    public String saksId = null;
    public String fagsystemSaksId = null;
    public String temaKode, temaNavn, fagsystemKode, fagsystemNavn, sakstype;
    public DateTime opprettetDato;
    public Boolean finnesIGsak = false, finnesIPsak = false;

    public static final String TEMAKODE_OPPFOLGING = "OPP";
    public static final String SAKSTYPE_GENERELL = "GEN";
    public static final String SAKSTYPE_MED_FAGSAK = "MFS";
    public static final String FAGSYSTEMKODE_ARENA = "AO01";
    public static final String FAGSYSTEMKODE_BIDRAG = "BISYS";

    public static final String FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK = "FS22";

    public static final List<String> GODKJENTE_TEMA_FOR_GENERELL_SAK = List.of("AAP", "AGR", "BAR", "BIL", "BID", "DAG", "ENF", "ERS", "EYO", "FEI", "FOR", "FOS", "FUL", "GEN", "GRA", "GRU", "HEL", "HJE", "IND", "KON", "KTR", "MED", "MOB", "OMS", "REH", "RVE", "RPO", "SAK", "SAP", "SER", "STO", "SUP", "SYK", "SYM", "TRK", "TRY", "TSR", "TSO", "UFM", "VEN", "YRA", "YRK", "FRI", TEMAKODE_OPPFOLGING,  "AKT", "POI", "PAI", "UNG");


    public boolean isSakstypeForVisningGenerell() {
        return SAKSTYPE_GENERELL.equals(sakstype);
    }

    public static final Predicate<JournalforingSak> IS_GENERELL_SAK = JournalforingSak::isSakstypeForVisningGenerell;

    public static Predicate<JournalforingSak> harTemaKode(final String temaKode) {
        return sak -> temaKode.equals(sak.temaKode);
    }

    public static final Predicate<JournalforingSak> IS_ARENA_OPPFOLGING = sak -> TEMAKODE_OPPFOLGING.equals(sak.temaKode)
            && FAGSYSTEMKODE_ARENA.equals(sak.fagsystemKode)
            && SAKSTYPE_MED_FAGSAK.equals(sak.sakstype);

    @JsonGetter
    public String getSaksIdVisning() {
        if(fagsystemSaksId != null) return fagsystemSaksId;
        else if (saksId != null) return saksId;
        else return "";
    }

    @Override
    public int compareTo(JournalforingSak other) {
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

        JournalforingSak sak = (JournalforingSak) obj;
        if (saksId != null && saksId.equals(sak.saksId)) {
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
        if (saksId != null) {
            return saksId.hashCode();
        } else if (temaKode != null
                && fagsystemKode != null
                && sakstype != null) {
            return temaKode.hashCode() * fagsystemKode.hashCode() * sakstype.hashCode();
        } else {
            return 0;
        }
    }
}
