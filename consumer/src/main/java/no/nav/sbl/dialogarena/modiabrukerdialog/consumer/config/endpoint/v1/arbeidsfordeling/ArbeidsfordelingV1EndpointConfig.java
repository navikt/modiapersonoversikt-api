package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.arbeidsfordeling;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.ArbeidsfordelingV1Mock;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.ArbeidsfordelingV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class ArbeidsfordelingV1EndpointConfig {

    @Bean
    public ArbeidsfordelingV1 arbeidsfordelingV1() {
        final ArbeidsfordelingV1 arbeidsfordelingV1 = lagEndpoint();

        return createTimerProxyForWebService("arbeidsfordelingV1", arbeidsfordelingV1, ArbeidsfordelingV1.class);
    }

    @Bean
    public Pingable arbeidsfordelingV1Ping() {
        return new PingableWebService("NORG2 - ArbeidsfordelingV1", lagEndpoint());
    }

    private ArbeidsfordelingV1 lagMockEnpoint() {
        return ArbeidsfordelingV1Mock.arbeidsfordelingV1();
    }

    private ArbeidsfordelingV1 lagEndpoint() {
        return new CXFClient<>(ArbeidsfordelingV1.class)
                .address(System.getProperty("arbeidsfordeling.v1.url"))
                .configureStsForSystemUser()
                .build();
    }
}
