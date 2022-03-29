package no.nav.modiapersonoversikt.commondomain;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class TemagruppeTemaMapping {
    public static final Temagruppe TEMA_UTEN_TEMAGRUPPE = Temagruppe.OVRG;

    public static final Map<String, String> TEMA_TEMAGRUPPE_MAPPING = new HashMap<String, String>() {
        {
            put("AAP", Temagruppe.ARBD.name());
            put("DAG", Temagruppe.ARBD.name());
            put("FOS", Temagruppe.ARBD.name());
            put("IND", Temagruppe.ARBD.name());
            put("MOB", Temagruppe.ARBD.name());
            put("OPP", Temagruppe.ARBD.name());
            put("REH", Temagruppe.ARBD.name());
            put("SAK", Temagruppe.ARBD.name());
            put("SAP", Temagruppe.ARBD.name());
            put("SYK", Temagruppe.ARBD.name());
            put("SYM", Temagruppe.ARBD.name());
            put("VEN", Temagruppe.ARBD.name());
            put("YRA", Temagruppe.ARBD.name());
            put("YRK", Temagruppe.ARBD.name());
            put("TSO", Temagruppe.ARBD.name());
            put("TSR", Temagruppe.ARBD.name());

            put("BAR", Temagruppe.FMLI.name());
            put("BID", Temagruppe.FMLI.name());
            put("ENF", Temagruppe.FMLI.name());
            put("FOR", Temagruppe.FMLI.name());
            put("GRA", Temagruppe.FMLI.name());
            put("GRU", Temagruppe.FMLI.name());
            put("KON", Temagruppe.FMLI.name());
            put("OMS", Temagruppe.FMLI.name());

            put("AAR", Temagruppe.OVRG.name());
            put("AGR", Temagruppe.OVRG.name());
            put("ERS", Temagruppe.OVRG.name());
            put("FEI", Temagruppe.OVRG.name());
            put("FUL", Temagruppe.OVRG.name());
            put("GEN", Temagruppe.OVRG.name());
            put("KLA", Temagruppe.OVRG.name());
            put("KNA", Temagruppe.OVRG.name());
            put("KTR", Temagruppe.OVRG.name());
            put("MED", Temagruppe.OVRG.name());
            put("RVE", Temagruppe.OVRG.name());
            put("RPO", Temagruppe.OVRG.name());
            put("SER", Temagruppe.OVRG.name());
            put("SIK", Temagruppe.OVRG.name());
            put("STO", Temagruppe.OVRG.name());
            put("TRK", Temagruppe.OVRG.name());
            put("TRY", Temagruppe.OVRG.name());
            put("UFM", Temagruppe.OVRG.name());

            put("PEN", Temagruppe.PENS.name());
            put("SUP", Temagruppe.PENS.name());
            put("UFO", Temagruppe.UFRT.name());

            put("HJE", Temagruppe.HJLPM.name());
            put("BIL", Temagruppe.BIL.name());
            put("HEL", Temagruppe.ORT_HJE.name());
        }
    };

    public static final Map<String, List<String>> TEMAGRUPPE_TEMA_MAPPING = new HashMap<String, List<String>>() {
        {
            put(Temagruppe.ARBD.name(), asList("AAP", "DAG", "FOS", "IND", "MOB", "OPP", "REH", "SAK", "SAP", "SYM", "VEN", "YRA", "YRK", "TSO", "TSR"));
            put(Temagruppe.HELSE.name(), emptyList());
            put(Temagruppe.FMLI.name(), asList("BAR", "BID", "ENF", "FOR", "GRA", "GRU", "KON", "OMS"));
            put(Temagruppe.FDAG.name(), emptyList());
            put(Temagruppe.OVRG.name(), asList("AAR", "AGR", "ERS", "FEI", "FUL", "GEN", "KLA", "KNA", "KTR", "MED", "RVE", "RPO", "SER", "SIK", "STO", "TRK", "TRY", "UFM"));
            put(Temagruppe.PENS.name(), asList("PEN", "SUP"));
            put(Temagruppe.HJLPM.name(), asList("HJE", "BIL", "HEL"));
            put(Temagruppe.BIL.name(), asList("HJE", "BIL", "HEL"));
            put(Temagruppe.ORT_HJE.name(), asList("HJE", "BIL", "HEL"));
            put(Temagruppe.UFRT.name(), singletonList("UFO"));
            put(Temagruppe.PLEIEPENGERSY.name(), singletonList("OMS"));
            put(Temagruppe.UTLAND.name(), new ArrayList<>(TEMA_TEMAGRUPPE_MAPPING.keySet()));
        }
    };

    public static List<String> hentTemaForTemagruppe(String temagruppe) {
        return TEMAGRUPPE_TEMA_MAPPING.get(temagruppe) == null ? Collections.emptyList() : TEMAGRUPPE_TEMA_MAPPING.get(temagruppe);
    }

    public static String hentTemagruppeForTema(String tema) {
        return TEMA_TEMAGRUPPE_MAPPING.get(tema) == null ?  TEMA_UTEN_TEMAGRUPPE.name() : TEMA_TEMAGRUPPE_MAPPING.get(tema);
    }
}
