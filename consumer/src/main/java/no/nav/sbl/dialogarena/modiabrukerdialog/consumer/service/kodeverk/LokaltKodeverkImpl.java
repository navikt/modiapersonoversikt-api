package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.LokaltKodeverk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

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

    @Override
    public Map<String, List<String>> hentTemagruppeTemaMapping() {
        return TEMAGRUPPE_TEMA_MAPPING;
    }

}
