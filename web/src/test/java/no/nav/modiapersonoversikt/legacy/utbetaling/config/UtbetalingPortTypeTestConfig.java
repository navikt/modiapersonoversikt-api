package no.nav.modiapersonoversikt.legacy.utbetaling.config;

import no.nav.common.cxf.CXFClient;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

@Configuration
public class UtbetalingPortTypeTestConfig {

    @Bean
    public UtbetalingV1 utbetalingPortType() {
        return createUtbetalingPortType();
    }

    private UtbetalingV1 createUtbetalingPortType() {
        return new CXFClient<>(UtbetalingV1.class)
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/utbetaling/v1/Binding", "Utbetaling_v1"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/utbetaling/v1/Binding", "Utbetaling_v1Port"))
                .address(EnvironmentUtils.getRequiredProperty("UTBETALING_V1_ENDPOINTURL"))
                .build();
    }

}
