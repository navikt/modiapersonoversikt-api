package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.TildelOppgaveV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class GsakTildelOppgaveV1EndpointConfig {
    @Autowired
    private StsConfig stsConfig;

    @Bean
    public TildelOppgaveV1 gsakTildelOppgavePortType() {
        TildelOppgaveV1 prod = lagEndpoint()
                .configureStsForSubject(stsConfig)
                .build();

        return createTimerProxyForWebService("TildelOppgaveV1", prod, TildelOppgaveV1.class);
    }

    @Bean
    public Pingable gsakTildelOppgavePing() {
        return new PingableWebService("Gsak - Tildel oppgave", lagEndpoint()
                .configureStsForSystemUser(stsConfig)
                .build());
    }

    private static CXFClient<TildelOppgaveV1> lagEndpoint() {
        return new CXFClient<>(TildelOppgaveV1.class)
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_OPPGAVEBEHANDLING_V3_ENDPOINTURL"));
    }

}
