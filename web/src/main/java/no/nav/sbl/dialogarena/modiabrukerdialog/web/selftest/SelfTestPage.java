package no.nav.sbl.dialogarena.modiabrukerdialog.web.selftest;

import no.nav.kjerneinfo.kontrakter.ping.KontrakterPing;
import no.nav.kjerneinfo.ping.KjerneinfoPing;
import no.nav.modig.core.exception.SystemException;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.wicket.selftest.SelfTestBase;
import no.nav.sykmeldingsperioder.ping.SykmeldingsperioderPing;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static org.slf4j.LoggerFactory.getLogger;

public class SelfTestPage extends SelfTestBase {

    private static final Logger logger = getLogger(SelfTestPage.class);

    @Inject
    @Named("personsokPing")
    private Pingable personsokPing;

    @Inject
    private KjerneinfoPing kjerneinfoPing;

    @Inject
    private KontrakterPing kontrakterPing;

    @Inject
    private SykmeldingsperioderPing sykmeldingsperioderPing;

    public SelfTestPage(PageParameters params) {
        super("Modiabrukerdialog", params);
    }

    @Override
    protected void addToStatusList(List<AvhengighetStatus> statusList) {
        statusList.addAll(getPingableComponentStatus(personsokPing));
        statusList.addAll(getPingableComponentStatus(kjerneinfoPing));
        statusList.addAll(getPingableComponentStatus(kontrakterPing));
        statusList.addAll(getPingableComponentStatus(sykmeldingsperioderPing));
    }

    private List<AvhengighetStatus> getPingableComponentStatus(Pingable pingable) {
        List<AvhengighetStatus> serviceStatuses = new ArrayList<>();
        try {
            List<PingResult> pingResults = pingable.ping();
            if (!pingResults.isEmpty()) {
                for (PingResult pingResult : pingResults) {
                    String status = pingResult.getServiceStatus().equals(SERVICE_OK) ? SelfTestBase.STATUS_OK : SelfTestBase.STATUS_ERROR;
                    serviceStatuses.add(new AvhengighetStatus(pingResult.getServiceName().toUpperCase() + "_PING",status, pingResult.getElapsedTime()));
                }
            }
        } catch (SystemException se) {
            logger.warn("Service was not retrievable. Class canonical name: " + pingable.getClass().getCanonicalName() + ". Exception message: " + se.getMessage());
        }
        return serviceStatuses;
    }

}
