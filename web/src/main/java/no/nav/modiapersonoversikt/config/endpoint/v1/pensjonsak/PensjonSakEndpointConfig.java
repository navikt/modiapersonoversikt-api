package no.nav.modiapersonoversikt.config.endpoint.v1.pensjonsak;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class PensjonSakEndpointConfig {
    @Autowired
    private StsConfig stsConfig;

    @Bean
    public PensjonSakV1 pensjonSakV1() {
        PensjonSakV1 prod = createPensjonSakV1().configureStsForSubject(stsConfig).build();
        return createTimerProxyForWebService("PensjonSakV1", prod, PensjonSakV1.class);
    }

    @Bean
    public Pingable pensjonSakV1Ping() {
        final PensjonSakV1 ws = createPensjonSakV1().configureStsForSystemUser(stsConfig).build();
        return new PingableWebService("Pesys - Pensjonsak", ws);
    }

    private static CXFClient<PensjonSakV1> createPensjonSakV1() {
        return new CXFClient<>(PensjonSakV1.class)
                .timeout(15000, 15000)
                .address(EnvironmentUtils.getRequiredProperty("PENSJON_PENSJONSAK_V1_ENDPOINTURL"));
    }
}
