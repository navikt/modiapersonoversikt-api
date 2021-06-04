package no.nav.modiapersonoversikt.integration.sykmeldingsperioder.consumer.pleiepenger;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.PleiepengerV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;


@Configuration
public class PleiepengerConsumerConfig {

    public static final String PLEIEPENGER_V1_ENDPOINT_KEY = "VIRKSOMHET_PLEIEPENGER_V1_ENDPOINTURL";

    @Autowired
    private StsConfig stsConfig;

    @Bean
    public PleiepengerV1 pleiepengerPortType() {
        PleiepengerV1 portType = lagEndpoint().configureStsForSubject(stsConfig).build();
        return createTimerProxyForWebService("Pleiepenger_v1", portType, PleiepengerV1.class);
    }

    private CXFClient<PleiepengerV1> lagEndpoint() {
        return new CXFClient<>(PleiepengerV1.class)
                .address(EnvironmentUtils.getRequiredProperty(PLEIEPENGER_V1_ENDPOINT_KEY));
    }

    @Bean
    public Pingable pleiepengerPingable() {
        return new PingableWebService("Pleiepenger", lagEndpoint().configureStsForSystemUser(stsConfig).build());
    }

}
