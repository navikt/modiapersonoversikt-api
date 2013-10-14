package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import java.util.LinkedList;
import java.util.List;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.nav.modig.modia.ping.PingResult.ServiceResult;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;

/**
 * Kaller ping()-operasjon på angitte webservice-grensesnitt
 */
public class BesvareHenvendelsePingImpl implements Pingable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private BesvareHenvendelsePortType besvareHenvendelsePortType;

    public BesvareHenvendelsePingImpl(BesvareHenvendelsePortType besvareHenvendelsePortType) {
        this.besvareHenvendelsePortType = besvareHenvendelsePortType;
    }

    @Override
    public List<PingResult> ping() {
        List<PingResult> pingResultList = new LinkedList<>();
        pingResultList.add(performPing());
        return pingResultList;
    }

    private PingResult performPing() {
        boolean pingResponseSuccessful;
        long timeElapsed;
        long start = System.currentTimeMillis();
        try {
            besvareHenvendelsePortType.ping();
            timeElapsed = System.currentTimeMillis() - start;
            logger.info("BesvareHenvendelse_v1.ping(): SUCCESS");
            pingResponseSuccessful = true;
        } catch (Exception e) {
            timeElapsed = System.currentTimeMillis() - start;
            logger.info("BesvareHenvendelse_v1.ping(): ERROR" + e.getMessage());
            pingResponseSuccessful = false;
        }

        ServiceResult serviceResult = pingResponseSuccessful ? SERVICE_OK : SERVICE_FAIL;

        return new PingResult("BesvareHenvendelse_v1", serviceResult, timeElapsed);
    }

}
