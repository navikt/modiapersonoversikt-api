package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak.GsakOppgaveV3EndpointConfig.GSAK_V3_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakOppgavebehandlingV3PortTypeMock.createOppgavebehandlingPortTypeMock;

@Configuration
public class GsakOppgavebehandlingV3EndpointConfig {

    @Bean
    public OppgavebehandlingV3 gsakOppgavebehandlingPortType() {
        OppgavebehandlingV3 prod = createOppgavebehandlingPortType().configureStsForOnBehalfOfWithJWT().build();
        OppgavebehandlingV3 mock = createOppgavebehandlingPortTypeMock();

        return createMetricsProxyWithInstanceSwitcher("OppgavebehandlingV3", prod, mock, GSAK_V3_KEY, OppgavebehandlingV3.class);
    }

    @Bean
    public Pingable gsakOppgavebehandlingPing() {
        final OppgavebehandlingV3 ws = createOppgavebehandlingPortType().configureStsForSystemUserInFSS().build();
        return new PingableWebService("Gsak - oppgavebehandling", ws);
    }

    private static CXFClient<OppgavebehandlingV3> createOppgavebehandlingPortType() {
        return new CXFClient<>(OppgavebehandlingV3.class)
                .address(System.getProperty("gsak.oppgavebehandling.v3.url"));
    }

}
