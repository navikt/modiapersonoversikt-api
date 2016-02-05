package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.sakogbehandling;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandling_v1PortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TimingMetricsProxy.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class SakOgBehandlingEndpointConfig {

    public static final String SAKOGBEHANDLING_KEY = "start.sakogbehandling.withmock";

    @Bean
    public SakOgBehandling_v1PortType sakOgBehandlingPortType() {
        final SakOgBehandling_v1PortType prod = createSakogbehandlingPortType(new UserSAMLOutInterceptor());
        final SakOgBehandling_v1PortType mock = new SakOgBehandlingPortTypeMock().getSakOgBehandlingPortTypeMock();

        return createMetricsProxyWithInstanceSwitcher(prod, mock, SAKOGBEHANDLING_KEY, SakOgBehandling_v1PortType.class);
    }

    @Bean
    public Pingable pingSakOgBehandling() {
        final SakOgBehandling_v1PortType ws = createSakogbehandlingPortType(new SystemSAMLOutInterceptor());
        return new PingableWebService("Sak og behandling", ws);
    }

    private SakOgBehandling_v1PortType createSakogbehandlingPortType(AbstractSAMLOutInterceptor interceptor) {
        return new CXFClient<>(SakOgBehandling_v1PortType.class)
                .wsdl("classpath:sakOgBehandling/no/nav/tjeneste/virksomhet/sakOgBehandling/v1/sakOgBehandling.wsdl")
                .address(System.getProperty("sakogbehandling.ws.url"))
                .withOutInterceptor(interceptor)
                .build();
    }

}
