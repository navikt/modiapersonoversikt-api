package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.TildelOppgaveV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakTildelOppgaveV1PortTypeMock.createTildelOppgavePortTypeMock;

@Configuration
public class GsakTildelOppgaveV1EndpointConfig {

    public static final String GSAK_TILDEL_OPPGAVE_KEY = "start.gsak.tildeloppgave.withmock";

    @Bean
    public TildelOppgaveV1 gsakTildelOppgavePortType() {
        TildelOppgaveV1 prod = lagEndpoint()
                .configureStsForOnBehalfOfWithJWT()
                .build();
        TildelOppgaveV1 mock = createTildelOppgavePortTypeMock();

        return createMetricsProxyWithInstanceSwitcher("TildelOppgaveV1", prod, mock, GSAK_TILDEL_OPPGAVE_KEY, TildelOppgaveV1.class);
    }

    @Bean
    public Pingable gsakTildelOppgavePing() {
        return new PingableWebService("Gsak - Tildel oppgave", lagEndpoint()
                .configureStsForSystemUserInFSS()
                .build());
    }

    private static CXFClient<TildelOppgaveV1> lagEndpoint() {
        return new CXFClient<>(TildelOppgaveV1.class)
                .address(EnvironmentUtils.getRequiredProperty("gsak.oppgavebehandling.v3.url"));
    }

}
