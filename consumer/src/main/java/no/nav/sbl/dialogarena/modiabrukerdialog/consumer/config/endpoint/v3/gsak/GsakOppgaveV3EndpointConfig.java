package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakOppgaveV3PortTypeMock.createOppgavePortTypeMock;

@Configuration
public class GsakOppgaveV3EndpointConfig {

    public static final String GSAK_V3_KEY = "start.gsak.oppgave.withmock";

    @Bean
    public OppgaveV3 gsakOppgavePortType() {
        OppgaveV3 prod = createOppgavePortType().configureStsForSubject().build();
        OppgaveV3 mock = createOppgavePortTypeMock();

        return createMetricsProxyWithInstanceSwitcher("OppgaveV3", prod, mock, GSAK_V3_KEY, OppgaveV3.class);
    }

    @Bean
    public Pingable gsakOppgavePing() {
        final OppgaveV3 ws = createOppgavePortType().configureStsForSystemUser().build();
        return new PingableWebService("Gsak - oppgave", ws);
    }

    private static CXFClient<OppgaveV3> createOppgavePortType() {
        return new CXFClient<>(OppgaveV3.class)
                .address(System.getProperty("gsak.oppgave.v3.url"));
    }

}
