package no.nav.sbl.dialogarena.mottaksbehandling.henvendelse;

import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.commons.collections15.Transformer;

import java.util.Comparator;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.not;
import static no.nav.modig.lang.collections.PredicateUtils.where;

public class Henvendelser {
    private HenvendelsePortType henvendelsePortType;

    public Henvendelser(HenvendelsePortType henvendelsePortType) {
        this.henvendelsePortType = henvendelsePortType;
    }

    public List<WSHenvendelse> tidligereDialog(String fnr, String traadId, String etterBehandlingsId) {
        List<WSHenvendelse> wsHenvendelser = on(henvendelsePortType.hentHenvendelseListe(fnr, asList("SPORSMAL", "SVAR"))).collect(NYESTE_FORST);
        return on(wsHenvendelser)
                .filter(where(TRAAD_ID, equalTo(traadId)))
                .filter(where(BEHANDLINGS_ID, not(equalTo(etterBehandlingsId)))).collect();
    }

    private static final Comparator<WSHenvendelse> NYESTE_FORST = new Comparator<WSHenvendelse>() {
        @Override
        public int compare(WSHenvendelse o1, WSHenvendelse o2) {
            return o2.getOpprettetDato().compareTo(o1.getOpprettetDato());
        }
    };

    private static final Transformer<WSHenvendelse, String> TRAAD_ID = new Transformer<WSHenvendelse, String>() {
        @Override
        public String transform(WSHenvendelse wsHenvendelse) {
            return wsHenvendelse.getTraad();
        }
    };
    private static final Transformer<WSHenvendelse, String> BEHANDLINGS_ID = new Transformer<WSHenvendelse, String>() {
        @Override
        public String transform(WSHenvendelse wsHenvendelse) {
            return wsHenvendelse.getBehandlingsId();
        }
    };
}
