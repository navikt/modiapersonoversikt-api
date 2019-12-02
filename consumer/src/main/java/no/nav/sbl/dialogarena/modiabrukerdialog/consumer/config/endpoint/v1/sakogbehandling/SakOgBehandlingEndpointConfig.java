package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.sakogbehandling;

import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class SakOgBehandlingEndpointConfig {

    @Bean
    public SakOgBehandlingV1 sakOgBehandlingPortType() {
        final SakOgBehandlingV1 prod = createSakogbehandlingPortType().configureStsForSubject().build();
        final SakOgBehandlingV1 mock = new SakOgBehandlingPortTypeMock().getSakOgBehandlingPortTypeMock();

        return createTimerProxyForWebService("SakOgBehandling", prod, SakOgBehandlingV1.class);
    }

    @Bean
    public Pingable pingSakOgBehandling() {
        final SakOgBehandlingV1 ws = createSakogbehandlingPortType().configureStsForSystemUser().build();
        return new PingableWebService("Sak og behandling", ws);
    }

    private CXFClient<SakOgBehandlingV1> createSakogbehandlingPortType() {
        return new CXFClient<>(SakOgBehandlingV1.class)
                .timeout(15000, 15000)
                .wsdl("classpath:sakOgBehandling/no/nav/tjeneste/virksomhet/sakOgBehandling/v1/sakOgBehandling.wsdl")
                .address(EnvironmentUtils.getRequiredProperty("SAKOGBEHANDLING_ENDPOINTURL"));
    }

}
