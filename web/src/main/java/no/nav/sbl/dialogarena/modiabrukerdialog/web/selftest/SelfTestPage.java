package no.nav.sbl.dialogarena.modiabrukerdialog.web.selftest;

import no.nav.modig.modia.ping.FailedPingResult;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.wicket.selftest.SelfTestBase;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                PingResult pingResult = pingable.ping();
                String serviceName = pingable.name();
                String methodName = pingable.method();
                String endpoint = pingable.endpoint();
                String status;
                if (pingResult.getServiceStatus().equals(SERVICE_OK)) {
                    status = STATUS_OK;
                }
                else if(pingResult.getServiceStatus().equals(PingResult.ServiceResult.UNPINGABLE)){
                    status = STATUS_UNPINGABLE;
                }
                else {status = STATUS_ERROR;}
                AvhengighetStatus avhengighetStatus = new AvhengighetStatus(serviceName, status, pingResult.getElapsedTime(), "", methodName, endpoint);
                if(pingResult instanceof FailedPingResult){
                    FailedPingResult failedPingResult = (FailedPingResult) pingResult;
                    avhengighetStatus.addExceptionMessage(ExceptionUtils.getMessage(failedPingResult.getThrowable()));
                    avhengighetStatus.addStackTrace(ExceptionUtils.getStackTrace(failedPingResult.getThrowable()));
                }
                serviceStatuses.add(avhengighetStatus);
            } catch (Exception e) {
                AvhengighetStatus avhengighetStatus = new AvhengighetStatus(pingable.name(), STATUS_ERROR, 0, "Feilet utenfor ping-metode");
                avhengighetStatus.addExceptionMessage(ExceptionUtils.getMessage(e));
                avhengighetStatus.addStackTrace(ExceptionUtils.getStackTrace(e));
                serviceStatuses.add(avhengighetStatus);
                logger.error("Pingable: " + pingable.name()+" failed. Class: " + pingable.getClass().getCanonicalName() + ". Exception message: " + e.getMessage(), e);
            }
        }
        return serviceStatuses;
    }

}
