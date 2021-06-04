package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.BrukervarselV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class VarslingEndpointConfig {
    @Autowired
    private StsConfig stsConfig;

    @Bean
    public BrukervarselV1 varslerPortType() {
        final BrukervarselV1 prod = createVarslingPortType().configureStsForSubject(stsConfig).build();
        return createTimerProxyForWebService("Varsler", prod, BrukervarselV1.class);
    }

    @Bean
    public Pingable varslerPing() {
        final BrukervarselV1 ws = createVarslingPortType().configureStsForSystemUser(stsConfig).build();
        return new PingableWebService("Varsler", ws);
    }

    private static CXFClient<BrukervarselV1> createVarslingPortType() {
        return new CXFClient<>(BrukervarselV1.class)
                .address(EnvironmentUtils.getRequiredProperty("BRUKERVARSELV1_ENDPOINTURL"));
    }
}
