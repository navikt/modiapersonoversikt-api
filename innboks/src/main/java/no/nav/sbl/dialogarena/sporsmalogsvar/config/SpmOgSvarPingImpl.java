package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Kaller ping()-operasjon på angitte webservice-grensesnitt
 */
public class SpmOgSvarPingImpl implements Pingable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private HenvendelsePortType henvendelsePortType;

    public SpmOgSvarPingImpl(HenvendelsePortType henvendelsePortType) {
        this.henvendelsePortType = henvendelsePortType;
    }

    @Override
    public List<PingResult> ping() {
        List<PingResult> pingResultList = new LinkedList<>();
        pingResultList.add(performHenvendelsePing());
        return pingResultList;
    }

    private PingResult performHenvendelsePing() {
        boolean pingResponseSuccessful;
        long timeElapsed;
        long start = System.currentTimeMillis();
        try {
            henvendelsePortType.ping();
            timeElapsed = System.currentTimeMillis() - start;
            logger.info("HenvendelseFelles_v1.ping(): SUCCESS");
            pingResponseSuccessful = true;
        } catch (Exception e) {
            timeElapsed = System.currentTimeMillis() - start;
            logger.info("HenvendelseFelles_v1.ping(): ERROR" + e.getMessage());
            pingResponseSuccessful = false;
        }

        PingResult.ServiceResult serviceResult;

        if (pingResponseSuccessful) {
            serviceResult = PingResult.ServiceResult.SERVICE_OK;
        } else {
            serviceResult = PingResult.ServiceResult.SERVICE_FAIL;
        }
        return new PingResult("HenvendelseFelles_v1", serviceResult, timeElapsed);
    }

}
