package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.commons.collections15.Transformer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;

public class HenvendelseService {

    private List<WSHenvendelse> alleHenvendelser;

    public HenvendelseService(HenvendelsePortType service, String fnr) {
        alleHenvendelser = on(service.hentHenvendelseListe(fnr, asList("SPORSMAL", "SVAR"))).collect(NYESTE_OVERST);
    }

    public List<WSHenvendelse> alleHenvendelser() {
        return alleHenvendelser;
    }

    public List<List<WSHenvendelse>> alleTraader() {
        List<List<WSHenvendelse>> alleTraader = new ArrayList<>();
        for (String traadId : on(alleHenvendelser).map(TRAAD_ID).collectIn(new HashSet<String>())) {
            alleTraader.add(on(alleHenvendelser).filter(where(TRAAD_ID, equalTo(traadId))).collect(NYESTE_OVERST));
        }
        return alleTraader;
    }

    public static final Transformer<WSHenvendelse, String> TRAAD_ID = new Transformer<WSHenvendelse, String>() {
        @Override
        public String transform(WSHenvendelse wsHenvendelse) {
            return wsHenvendelse.getTraad();
        }
    };

    public static final Comparator<WSHenvendelse> NYESTE_OVERST = new Comparator<WSHenvendelse>() {
        @Override
        public int compare(WSHenvendelse o1, WSHenvendelse o2) {
            return o2.getOpprettetDato().compareTo(o1.getOpprettetDato());
        }
    };

}
