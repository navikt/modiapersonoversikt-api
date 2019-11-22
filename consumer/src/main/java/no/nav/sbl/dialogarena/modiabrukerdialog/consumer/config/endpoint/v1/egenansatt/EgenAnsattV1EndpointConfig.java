package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.egenansatt;

import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.pip.egen.ansatt.v1.EgenAnsattV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class EgenAnsattV1EndpointConfig {

    @Bean
    public EgenAnsattV1 egenAnsattV1() {
        final EgenAnsattV1 egenAnsattV1 = lagEndpoint();
        final EgenAnsattV1 egenAnsattV1Mock = lagMockEndpoint();

        return createTimerProxyForWebService("egenAnsattV1", egenAnsattV1, EgenAnsattV1.class);
    }

    private EgenAnsattV1 lagMockEndpoint() {
        return EgenAnsattV1Mock.egenAnsattV1();
    }


    @Bean
    public Pingable egenAnsattPing() {
        return new PingableWebService("EgenAnsatt", lagEndpoint());
    }


    private EgenAnsattV1 lagEndpoint() {
        return new CXFClient<>(EgenAnsattV1.class)
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_EGENANSATT_V1_ENDPOINTURL"))
                .configureStsForSystemUser()
                .build();
    }

}
