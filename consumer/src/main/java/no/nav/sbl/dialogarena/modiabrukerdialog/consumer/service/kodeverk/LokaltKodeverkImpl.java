package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.LokaltKodeverk;

import java.util.*;
import java.util.Map.Entry;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe.ARBD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe.BIL;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe.FMLI;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe.HJLPM;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe.ORT_HJE;

public class LokaltKodeverkImpl implements LokaltKodeverk {

    public static final Map<String, List<String>> TEMAGRUPPE_TEMA_MAPPING = new HashMap<String, List<String>>() {
        {
            put(ARBD.name(), asList("AAP", "DAG", "FOS", "IND", "MOB", "OPP", "REH", "SAK", "SAP", "SYK", "SYM", "VEN", "YRA", "YRK"));
            put(FMLI.name(), asList("BAR", "BID", "ENF", "FOR", "GRA", "GRU", "KON", "OMS"));
            put(HJLPM.name(), asList("HJE"));
            put(BIL.name(), asList("BIL"));
            put(ORT_HJE.name(), asList("HEL"));
            put("OVRG", asList("AAR", "AGR", "FEI", "FUL", "GEN", "KLA", "KNA", "KTR", "MED", "SER", "SIK", "STO", "TRK", "TRY"));
            put("PENS", asList("PEN", "SUP", "UFO"));
        }
    };

    public static final Map<String, String> TEMA_TEMAGRUPPE_MAPPING = new HashMap<>();

    static {
        for (Entry<String, List<String>> mapping : TEMAGRUPPE_TEMA_MAPPING.entrySet()) {
            String temagruppe = mapping.getKey();
            for (String tema : mapping.getValue()) {
                TEMA_TEMAGRUPPE_MAPPING.put(tema, temagruppe);
            }
        }
    }

    @Override
    public String hentTemagruppeForTema(String tema, String defaultVerdi) {
        return optional(TEMA_TEMAGRUPPE_MAPPING.get(tema)).getOrElse(defaultVerdi);
    }

    public Map<String, String> hentTemaTemagruppeMapping() {
        return TEMA_TEMAGRUPPE_MAPPING;
    }

    public Map<String, List<String>> hentTemagruppeTemaMapping() {
        return TEMAGRUPPE_TEMA_MAPPING;
    }

}
