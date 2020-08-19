package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.utbetaling;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import javax.xml.namespace.QName;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class UtbetalingEndpointConfig {
    @Autowired
    private StsConfig stsConfig;

    @Bean
    public UtbetalingV1 utbetalingV1() {
        final UtbetalingV1 prod = createUtbetalingPortType().configureStsForSubject(stsConfig).build();

        return createTimerProxyForWebService("UtbetalingV1", prod, UtbetalingV1.class);
    }

    @Bean
    public UtbetalingPing pingUtbetalingV1() {
        UtbetalingV1 pingPorttype = createUtbetalingPortType()
                .configureStsForSystemUser(stsConfig)
                .build();
        return new UtbetalingPing("Utbetaling", pingPorttype);
    }

    private CXFClient<UtbetalingV1> createUtbetalingPortType() {
        return new CXFClient<>(UtbetalingV1.class)
                .wsdl("classpath:wsdl/utbetaling/no/nav/tjeneste/virksomhet/utbetaling/v1/Binding.wsdl")
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/utbetaling/v1/Binding", "Utbetaling_v1"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/utbetaling/v1/Binding", "Utbetaling_v1Port"))
                .address(EnvironmentUtils.getRequiredProperty("UTBETALING_V1_ENDPOINTURL"));
    }

    public class UtbetalingPing extends PingableWebService {

        public UtbetalingPing(String name, Object webservice) {
            super(name, webservice);
        }
    }
}


