package no.nav.sbl.dialogarena.modiabrukerdialog.web.selftest;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.wicket.selftest.SelfTestBase;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.utbetaling.UtbetalingEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static org.slf4j.LoggerFactory.getLogger;

public class SelfTestPage extends SelfTestBase {

    private static final Logger logger = getLogger(SelfTestPage.class);

    public SelfTestPage(PageParameters params) throws IOException {
        super("Modiabrukerdialog", params);
    }

    @Override
    protected void addToStatusList(List<AvhengighetStatus> statusList) {
        ApplicationContext applicationContext = WicketApplication.get().getApplicationContext();
        statusList.addAll(getPingableComponentStatus(applicationContext.getBeansOfType(Pingable.class).values()));
    }

    private List<AvhengighetStatus> getPingableComponentStatus(Iterable<Pingable> pingables) {
        List<AvhengighetStatus> serviceStatuses = new ArrayList<>();
        for (Pingable pingable : pingables) {
            try {
                if(shouldServiceBeIncluded(pingable)) {
                    List<PingResult> pingResults = pingable.ping();
                    for (PingResult pingResult : pingResults) {
                        String serviceName = pingResult.getServiceName().toUpperCase();
                        String status = pingResult.getServiceStatus().equals(SERVICE_OK) ? STATUS_OK : STATUS_ERROR;
                        serviceStatuses.add(new AvhengighetStatus(serviceName + "_PING", status, pingResult.getElapsedTime()));
                    }
                }
            } catch (Exception e) {
                logger.warn("Service was not retrievable. Class: " + pingable.getClass().getCanonicalName() + ". Exception message: " + e.getMessage(), e);
            }
        }
        return serviceStatuses;
    }

    /**
     * True hvis tjenesten skal inkluderes i selftesten
     * @return
     */
    private boolean shouldServiceBeIncluded(Pingable pingable) {
        Map<Class, String> services = possibleServicesToBeExcluded();
        if(services.containsKey(pingable.getClass())) {
            return Boolean.valueOf(System.getProperty(services.get(pingable.getClass()), "true"));
        }
        return true;
    }

    /**
     * Map av properties hvor key er Pingable class og value er systemProperty som styrer
     * om tjenesten skal legges til i selftesten eller ikke.
     *
     * @return
     */
    private Map<Class, String> possibleServicesToBeExcluded() {
        Map<Class, String> serviceMap = new HashMap<Class, String>();
        serviceMap.put(UtbetalingEndpointConfig.UtbetalingPing.class, "visUtbetalinger");
        return serviceMap;
    }

}
