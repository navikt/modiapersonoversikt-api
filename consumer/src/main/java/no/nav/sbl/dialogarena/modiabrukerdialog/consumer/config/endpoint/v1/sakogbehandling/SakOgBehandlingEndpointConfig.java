package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.sakogbehandling;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandling_v1PortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class SakOgBehandlingEndpointConfig {

    public static final String SAKOGBEHANDLING_KEY = "start.sakogbehandling.withmock";

    @Bean
    public SakOgBehandling_v1PortType sakOgBehandlingPortType() {
        final SakOgBehandling_v1PortType prod = createSakogbehandlingPortType().configureStsForOnBehalfOfWithJWT().build();
        final SakOgBehandling_v1PortType mock = new SakOgBehandlingPortTypeMock().getSakOgBehandlingPortTypeMock();

        return createMetricsProxyWithInstanceSwitcher("SakOgBehandling", prod, mock, SAKOGBEHANDLING_KEY, SakOgBehandling_v1PortType.class);
    }

    @Bean
    public Pingable pingSakOgBehandling() {
        final SakOgBehandling_v1PortType ws = createSakogbehandlingPortType().configureStsForSystemUserInFSS().build();
        return new PingableWebService("Sak og behandling", ws);
    }

    private CXFClient<SakOgBehandling_v1PortType> createSakogbehandlingPortType() {
        return new CXFClient<>(SakOgBehandling_v1PortType.class)
                .timeout(15000, 15000)
                .wsdl("classpath:sakOgBehandling/no/nav/tjeneste/virksomhet/sakOgBehandling/v1/sakOgBehandling.wsdl")
                .address(System.getProperty("sakogbehandling.ws.url"));
    }

}
