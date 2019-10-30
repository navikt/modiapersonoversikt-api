package no.nav.kjerneinfo.consumer.fim.behandleperson.config;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.UnpingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.behandleperson.v1.BehandlePersonV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class BehandlePersonEndpointConfig {

    private final String address = "";

    @Bean
    public BehandlePersonV1 behandlePersonV1() {
        final BehandlePersonV1 behandlePersonV1 = lagEndpoint().configureStsForSubject().build();

        return createTimerProxyForWebService("BehandlePersonV1", behandlePersonV1, BehandlePersonV1.class);
    }

    @Bean
    public Pingable personPing() {
        return new UnpingableWebService("TPS - BehandlePersonV1 (feature togglet av)", address);
    }

    private CXFClient<BehandlePersonV1> lagEndpoint() {
        return new CXFClient<>(BehandlePersonV1.class)
                .address(System.getProperty("tps.behandleperson.v1.url"));
    }

}
