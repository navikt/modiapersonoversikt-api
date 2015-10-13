package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;

public class TemagruppeTemaMapping {

    public static final Temagruppe TEMA_UTEN_TEMAGRUPPE = Temagruppe.OVRG;

    public static final Map<String, List<String>> TEMAGRUPPE_TEMA_MAPPING = new HashMap<String, List<String>>() {
        {
            put(Temagruppe.ARBD.name(), asList("AAP", "DAG", "FOS", "IND", "MOB", "OPP", "REH", "SAK", "SAP", "SYK", "SYM", "VEN", "YRA", "YRK"));
            put(Temagruppe.FMLI.name(), asList("BAR", "BID", "ENF", "FOR", "GRA", "GRU", "KON", "OMS"));
            put(Temagruppe.OVRG.name(), asList("AAR", "AGR", "FEI", "FUL", "GEN", "KLA", "KNA", "KTR", "MED", "SER", "SIK", "STO", "TRK", "TRY"));
            put(Temagruppe.PENS.name(), asList("PEN", "SUP", "UFO"));
            put(Temagruppe.HJLPM.name(), asList("HJE", "BIL", "HEL"));
            put(Temagruppe.BIL.name(), asList("HJE", "BIL", "HEL"));
            put(Temagruppe.ORT_HJE.name(), asList("HJE", "BIL", "HEL"));
        }
    };

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
            put("FEI", Temagruppe.OVRG.name());
            put("FUL", Temagruppe.OVRG.name());
            put("GEN", Temagruppe.OVRG.name());
            put("KLA", Temagruppe.OVRG.name());
            put("KNA", Temagruppe.OVRG.name());
            put("KTR", Temagruppe.OVRG.name());
            put("MED", Temagruppe.OVRG.name());
            put("SER", Temagruppe.OVRG.name());
            put("SIK", Temagruppe.OVRG.name());
            put("STO", Temagruppe.OVRG.name());
            put("TRK", Temagruppe.OVRG.name());
            put("TRY", Temagruppe.OVRG.name());

            put("PEN", Temagruppe.PENS.name());
            put("SUP", Temagruppe.PENS.name());
            put("UFO", Temagruppe.PENS.name());

            put("HJE", Temagruppe.HJLPM.name());
            put("BIL", Temagruppe.BIL.name());
            put("HEL", Temagruppe.ORT_HJE.name());
        }
    };

    public static List<String> hentTemaForTemagruppe(String temagruppe) {
        return optional(TEMAGRUPPE_TEMA_MAPPING.get(temagruppe)).getOrElse(Collections.<String>emptyList());
    }

    public static String hentTemagruppeForTema(String tema) {
        return optional(TEMA_TEMAGRUPPE_MAPPING.get(tema)).getOrElse(TEMA_UTEN_TEMAGRUPPE.name());
    }
}
