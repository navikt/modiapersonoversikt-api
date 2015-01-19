package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.LokaltKodeverk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;

public class LokaltKodeverkImpl implements LokaltKodeverk {

    public static final Map<String, List<String>> TEMAGRUPPE_TEMA_MAPPING = new HashMap<String, List<String>>() {
        {
            put("ARBD", asList("DAG", "AAP", "FOS", "IND", "OPP", "SYK", "SYM", "VEN", "YRK"));
            put("FMLI", asList("FOR", "BAR", "BID", "ENF", "GRA", "GRU", "KON", "OMS"));
            put("HJLPM", asList("BIL", "HEL", "HJE", "MOB"));
            put("OVRG", asList("FUL", "MED", "SER", "TRK", "AGR"));
            put("PENS", asList("PEN", "UFO"));
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
    public Map<String, List<String>> hentTemagruppeTemaMapping() {
        return TEMAGRUPPE_TEMA_MAPPING;
    }

    public Map<String, String> hentTemaTemagruppeMapping() {
        return TEMA_TEMAGRUPPE_MAPPING;
    }

    public String hentTemagruppeForTema(String tema) {
        return optional(TEMA_TEMAGRUPPE_MAPPING.get(tema)).getOrElse("ARBD");
    }

}
