package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.utbetaling;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.UtbetalingPortTypeMock;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.metrics.TimingMetricsProxy.createMetricsProxy;

@Configuration
public class UtbetalingEndpointConfig {

    public static final String UTBETALING_KEY = "start.utbetaling.withmock";

    @Bean(name = "utbetalingV1")
    public UtbetalingV1 utbetalingV1() {
        final UtbetalingV1 prod = createMetricsProxy(createUtbetalingPortType(new UserSAMLOutInterceptor()), UtbetalingV1.class);
        final UtbetalingV1 mock = new UtbetalingPortTypeMock().utbetalingPortType();

        return createSwitcher(prod, mock, UTBETALING_KEY, UtbetalingV1.class);
    }

    @Bean
    public Pingable pingUtbetalingV1(final UtbetalingV1 ws) {
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                long start = currentTimeMillis();
                String name = "UTBETALING";
                try {
                    ws.ping();
                    return Arrays.asList(new PingResult(name, SERVICE_OK, currentTimeMillis() - start));
                } catch (Exception e) {
                    return Arrays.asList(new PingResult(name, SERVICE_FAIL, currentTimeMillis() - start));
                }
            }
        };
    }

    private UtbetalingV1 createUtbetalingPortType(AbstractSAMLOutInterceptor interceptor) {
        CXFClient<UtbetalingV1> cxfClient = new CXFClient<>(UtbetalingV1.class)
                .wsdl("classpath:utbetaling/no/nav/tjeneste/virksomhet/utbetaling/v1/Binding.wsdl")
                .address(System.getProperty("utbetalingendpoint.url"))
                .withOutInterceptor(interceptor);
        cxfClient.factoryBean.setServiceName(new QName("http://nav.no/tjeneste/virksomhet/utbetaling/v1/Binding", "Utbetaling_v1"));
        cxfClient.factoryBean.setEndpointName(new QName("http://nav.no/tjeneste/virksomhet/utbetaling/v1/Binding", "Utbetaling_v1Port"));
        return cxfClient.build();
    }
}
