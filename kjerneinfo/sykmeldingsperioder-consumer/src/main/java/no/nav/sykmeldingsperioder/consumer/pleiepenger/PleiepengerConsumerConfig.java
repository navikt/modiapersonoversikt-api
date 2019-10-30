package no.nav.sykmeldingsperioder.consumer.pleiepenger;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.PleiepengerV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;


@Configuration
public class PleiepengerConsumerConfig {

    public static final String PLEIEPENGER_V1_ENDPOINT_KEY = "pleiepengerendpoint.url";

    @Bean
    public PleiepengerV1 pleiepengerPortType() {
        PleiepengerV1 portType = lagEndpoint().configureStsForSubject().build();
        return createTimerProxyForWebService("Pleiepenger_v1", portType, PleiepengerV1.class);
    }

    private CXFClient<PleiepengerV1> lagEndpoint() {
        return new CXFClient<>(PleiepengerV1.class)
                .address(System.getProperty(PLEIEPENGER_V1_ENDPOINT_KEY));
    }

    @Bean
    public Pingable pleiepengerPingable() {
        return new PingableWebService("Pleiepenger", lagEndpoint().configureStsForSystemUser().build());
    }

}
