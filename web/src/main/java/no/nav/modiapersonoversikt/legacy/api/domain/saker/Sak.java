package no.nav.modiapersonoversikt.legacy.api.domain.saker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sak implements Serializable, Comparable<Sak> {

    private static final Locale locale = Locale.getDefault();

    public String saksId = null;
    public String fagsystemSaksId = null;
    public String temaKode, temaNavn, fagsystemKode, fagsystemNavn, sakstype;
    public DateTime opprettetDato;
    public Boolean finnesIGsak = false, finnesIPsak = false;
    /**
     * Brukes for å markere saker som må spesial-håndteres
     */
    public Boolean syntetisk = false;

    public static final String TEMAKODE_OPPFOLGING = "OPP";
    public static final String TEMAKODE_KLAGE_ANKE = "KLA";
    public static final String SAKSTYPE_GENERELL = "GEN";
    public static final String SAKSTYPE_MED_FAGSAK = "MFS";
    public static final String FAGSYSTEMKODE_ARENA = "AO01";
    public static final String FAGSYSTEMKODE_PSAK = "PP01";
    public static final String FAGSYSTEMKODE_BIDRAG = "BID";
    public static final String FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK = "FS22";
    public static final String BIDRAG_MARKOR = "BID-HACK";

    public static final List<String> GYLDIGE_FAGSYSTEM_FOR_GENERELLE_SAKER = List.of(FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK, "");
    public static final List<String> GODKJENTE_TEMA_FOR_GENERELL_SAK = List.of("AAP", "AGR", "BAR", "BIL", "DAG", "ENF", "ERS", "FEI", "FOR", "FOS", "FUL", "GEN", "GRA", "GRU", "HEL", "HJE", "IND", "KON", "KTR", "MED", "MOB", "OMS", "REH", "RVE", "RPO", "SAK", "SAP", "SER", "STO", "SUP", "SYK", "SYM", "TRK", "TRY", "TSR", "TSO", "UFM", "VEN", "YRA", "YRK", "FRI", TEMAKODE_OPPFOLGING, BIDRAG_MARKOR);
    public static final List<String> GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER = List.of(FAGSYSTEMKODE_BIDRAG, FAGSYSTEMKODE_ARENA, FAGSYSTEMKODE_PSAK, "IT01", "OEBS", "V2", "AO11", "FS36", "FS38", "K9", "SUPSTONAD");

    public boolean isSakstypeForVisningGenerell() {
        return SAKSTYPE_GENERELL.equals(sakstype);
    }

    public static final Function<Sak, String> TEMAKODE = sak -> sak.temaKode;

    public static final Predicate<Sak> IS_GENERELL_SAK = Sak::isSakstypeForVisningGenerell;

    public static final Predicate<Sak> IS_GODKJENT_FAGSYSTEM_FOR_GENERELLE = sak -> GYLDIGE_FAGSYSTEM_FOR_GENERELLE_SAKER.contains(sak.fagsystemKode);

    public static final Predicate<Sak> IS_GODKJENT_TEMA_FOR_GENERELLE = sak -> GODKJENTE_TEMA_FOR_GENERELL_SAK.contains(sak.temaKode);

    public static final Predicate<Sak> IS_GODKJENT_FAGSYSTEM_FOR_FAGSAK = sak -> GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.contains(sak.fagsystemKode);

    public static Predicate<Sak> harTemaKode(final String temaKode) {
        return sak -> temaKode.equals(sak.temaKode);
    }

    public static final Predicate<Sak> IS_ARENA_OPPFOLGING = sak -> TEMAKODE_OPPFOLGING.equals(sak.temaKode)
            && FAGSYSTEMKODE_ARENA.equals(sak.fagsystemKode)
            && SAKSTYPE_MED_FAGSAK.equals(sak.sakstype);

    public String getOpprettetDatoFormatert() {
        return opprettetDato == null ? "" : DateTimeFormat.forPattern("d. MMM. yyyy").withLocale(locale).print(opprettetDato);
    }

    public String getSaksIdVisning() {
        if(fagsystemSaksId != null) return fagsystemSaksId;
        else if (saksId != null) return saksId;
        else return "";
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
