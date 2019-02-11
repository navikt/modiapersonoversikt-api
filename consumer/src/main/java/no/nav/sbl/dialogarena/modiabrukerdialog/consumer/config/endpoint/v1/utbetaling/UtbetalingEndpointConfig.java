package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.utbetaling;

import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.UtbetalingPortTypeMock;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class UtbetalingEndpointConfig {

    public static final String UTBETALING_KEY = "start.utbetaling.withmock";

    @Bean(name = "utbetalingV1")
    public UtbetalingV1 utbetalingV1() {
        final UtbetalingV1 prod = createUtbetalingPortType().configureStsForOnBehalfOfWithJWT().build();
        final UtbetalingV1 mock = new UtbetalingPortTypeMock().utbetalingPortType();

        return createMetricsProxyWithInstanceSwitcher("UtbetalingV1", prod, mock, UTBETALING_KEY, UtbetalingV1.class);
    }

    @Bean
    public UtbetalingPing pingUtbetalingV1() {
        UtbetalingV1 pingPorttype = createUtbetalingPortType()
                .configureStsForSystemUserInFSS()
                .build();
        return new UtbetalingPing("Utbetaling", pingPorttype);
    }

    private CXFClient<UtbetalingV1> createUtbetalingPortType() {
        return new CXFClient<>(UtbetalingV1.class)
                .wsdl("classpath:utbetaling/no/nav/tjeneste/virksomhet/utbetaling/v1/Binding.wsdl")
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/utbetaling/v1/Binding", "Utbetaling_v1"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/utbetaling/v1/Binding", "Utbetaling_v1Port"))
                .address(EnvironmentUtils.getRequiredProperty("utbetalingendpoint.url"));
    }

    public class UtbetalingPing extends PingableWebService {

        public UtbetalingPing(String name, Object webservice) {
            super(name, webservice);
        }
    }
}


