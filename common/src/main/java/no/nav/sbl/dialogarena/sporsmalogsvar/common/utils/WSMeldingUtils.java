package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding;
import org.apache.commons.collections15.Transformer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;

public class WSMeldingUtils {

    /**
     * Deler inn en liste henvendelser i tråder
     * @param wsMeldinger liste med henvendelser
     * @return map med key: trådid og value: alle henvendelser som tilhører tråden
     */
    public static Map<String, List<WSMelding>> skillUtTraader(List<WSMelding> wsMeldinger) {
        Map<String, List<WSMelding>> traaderMap = new HashMap<>();
        for (String traadId : on(wsMeldinger).map(TRAAD_ID).collectIn(new HashSet<String>())) {
            traaderMap.put(traadId, on(wsMeldinger).filter(where(TRAAD_ID, equalTo(traadId))).collect());
        }
        return traaderMap;
    }


    public static final Transformer<WSMelding, String> TRAAD_ID = new Transformer<WSMelding, String>() {
        @Override
        public String transform(WSMelding wsMelding) {
            return wsMelding.getTraad();
        }
    };

}
