package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;

import java.util.LinkedList;
import java.util.List;

/**
 * Kaller ping()-operasjon på angitte webservice-grensesnitt
 */
public class PingableImpl implements Pingable {

    private SporsmalOgSvarPortType sporsmalOgSvarPortType;

    public PingableImpl(SporsmalOgSvarPortType sporsmalOgSvarPortType) {
        this.sporsmalOgSvarPortType = sporsmalOgSvarPortType;
    }

    @Override
    public List<PingResult> ping() {
        List<PingResult> pingResultList = new LinkedList<>();
        sporsmalOgSvarPortType.ping();
        //add pingResultList
        return pingResultList;
    }
}
