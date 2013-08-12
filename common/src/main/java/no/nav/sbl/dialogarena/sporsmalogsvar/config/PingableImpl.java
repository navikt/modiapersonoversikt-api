package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * Kaller ping()-operasjon på angitte webservice-grensesnitt
 */
public class PingableImpl implements Pingable {

    private SporsmalOgSvarPortType sporsmalOgSvarPortType;
//    private HenvendelsePortType henvendelsePortType;

    public PingableImpl(SporsmalOgSvarPortType sporsmalOgSvarPortType, HenvendelsePortType henvendelsePortType) {
        this.sporsmalOgSvarPortType = sporsmalOgSvarPortType;
//        this.henvendelsePortType = henvendelsePortType;
    }

    @Override
    public List<PingResult> ping() {
        List<PingResult> pingResultList = new LinkedList<>();
//        pingResultList.add(sporsmalOgSvarPortType.ping());
//        pingResultList.add(henvendelsePortType.ping());
        return pingResultList;
    }
}
