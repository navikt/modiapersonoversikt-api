package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.egenansatt;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.pip.egen.ansatt.v1.EgenAnsattV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class EgenAnsattV1EndpointConfig {
    @Inject
    private StsConfig stsConfig;

    @Bean
    public EgenAnsattV1 egenAnsattV1() {
        final EgenAnsattV1 egenAnsattV1 = lagEndpoint();

        return createTimerProxyForWebService("egenAnsattV1", egenAnsattV1, EgenAnsattV1.class);
    }

    @Bean
    public Pingable egenAnsattPing() {
        return new PingableWebService("EgenAnsatt", lagEndpoint());
    }


    private EgenAnsattV1 lagEndpoint() {
        return new CXFClient<>(EgenAnsattV1.class)
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_EGENANSATT_V1_ENDPOINTURL"))
                .configureStsForSystemUser(stsConfig)
                .build();
    }

}
