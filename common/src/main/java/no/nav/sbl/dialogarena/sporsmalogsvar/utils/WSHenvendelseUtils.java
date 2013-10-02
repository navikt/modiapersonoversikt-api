package no.nav.sbl.dialogarena.sporsmalogsvar.utils;

import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.commons.collections15.Transformer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;

public class WSHenvendelseUtils {

    /**
     * Deler inn en liste henvendelser i tråder
     * @param wsHenvendelser liste med henvendelser
     * @return map med key: trådid og value: alle henvendelser som tilhører tråden
     */
    public static Map<String, List<WSHenvendelse>> skillUtTraader(List<WSHenvendelse> wsHenvendelser) {
        Map<String, List<WSHenvendelse>> traaderMap = new HashMap<>();
        for (String traadId : on(wsHenvendelser).map(TRAAD_ID).collectIn(new HashSet<String>())) {
            traaderMap.put(traadId, on(wsHenvendelser).filter(where(TRAAD_ID, equalTo(traadId))).collect());
        }
        return traaderMap;
    }

    public static final Comparator<WSHenvendelse> NYESTE_OVERST = new Comparator<WSHenvendelse>() {
        @Override
        public int compare(WSHenvendelse o1, WSHenvendelse o2) {
            return o2.getOpprettetDato().compareTo(o1.getOpprettetDato());
        }
    };

    public static final Transformer<WSHenvendelse, String> TRAAD_ID = new Transformer<WSHenvendelse, String>() {
        @Override
        public String transform(WSHenvendelse wsHenvendelse) {
            return wsHenvendelse.getTraad();
        }
    };

}
