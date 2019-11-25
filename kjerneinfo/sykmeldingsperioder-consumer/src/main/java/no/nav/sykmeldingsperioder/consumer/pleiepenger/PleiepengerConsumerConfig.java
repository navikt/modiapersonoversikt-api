package no.nav.sykmeldingsperioder.consumer.pleiepenger;

import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.PleiepengerV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;


@Configuration
public class PleiepengerConsumerConfig {

    public static final String PLEIEPENGER_V1_ENDPOINT_KEY = "VIRKSOMHET_PLEIEPENGER_V1_ENDPOINTURL";

    @Bean
    public PleiepengerV1 pleiepengerPortType() {
        PleiepengerV1 portType = lagEndpoint().configureStsForSubject().build();
        return createTimerProxyForWebService("Pleiepenger_v1", portType, PleiepengerV1.class);
    }

    private CXFClient<PleiepengerV1> lagEndpoint() {
        return new CXFClient<>(PleiepengerV1.class)
                .address(EnvironmentUtils.getRequiredProperty(PLEIEPENGER_V1_ENDPOINT_KEY));
    }

    @Bean
    public Pingable pleiepengerPingable() {
        return new PingableWebService("Pleiepenger", lagEndpoint().configureStsForSystemUser().build());
    }

}
