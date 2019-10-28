package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.TildelOppgaveV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class GsakTildelOppgaveV1EndpointConfig {

    @Bean
    public TildelOppgaveV1 gsakTildelOppgavePortType() {
        TildelOppgaveV1 prod = lagEndpoint()
                .configureStsForSubject()
                .build();

        return createTimerProxyForWebService("TildelOppgaveV1", prod, TildelOppgaveV1.class);
    }

    @Bean
    public Pingable gsakTildelOppgavePing() {
        return new PingableWebService("Gsak - Tildel oppgave", lagEndpoint()
                .configureStsForSystemUser()
                .build());
    }

    private static CXFClient<TildelOppgaveV1> lagEndpoint() {
        return new CXFClient<>(TildelOppgaveV1.class)
                .address(System.getProperty("gsak.oppgavebehandling.v3.url"));
    }

}
