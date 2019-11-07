package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.BrukervarselV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class VarslingEndpointConfig {

    @Bean
    public BrukervarselV1 varslerPortType() {
        final BrukervarselV1 prod = createVarslingPortType().configureStsForSubject().build();
        return createTimerProxyForWebService("Varsler", prod, BrukervarselV1.class);
    }

    @Bean
    public Pingable varslerPing() {
        final BrukervarselV1 ws = createVarslingPortType().configureStsForSystemUser().build();
        return new PingableWebService("Varsler", ws);
    }

    private static CXFClient<BrukervarselV1> createVarslingPortType() {
        return new CXFClient<>(BrukervarselV1.class)
                .address(System.getProperty("BRUKERVARSELV1_ENDPOINTURL"));
    }
}
