package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg2;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.OrganisasjonEnhetV1Mock;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.OrganisasjonEnhetV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TimingMetricsProxy.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class OrganisasjonEnhetEndpointConfig {

    private static final String NORG_KEY = "start.norg2.organisasjonenhet.withmock";

    @Bean
    public OrganisasjonEnhetV1 organisasjonEnhetV1() {
        final OrganisasjonEnhetV1 organisasjonEnhetV1 = lagEndpoint();
        final OrganisasjonEnhetV1 organisasjonEnhetV1Mock = lagMockEnpoint();

        return createMetricsProxyWithInstanceSwitcher(organisasjonEnhetV1, organisasjonEnhetV1Mock, NORG_KEY, OrganisasjonEnhetV1.class);
    }

    @Bean
    public Pingable gsakOrganisasjonEnhetPing() {
        return new PingableWebService("Gsak - sak", lagEndpoint());
    }

    private OrganisasjonEnhetV1 lagMockEnpoint() {
        return OrganisasjonEnhetV1Mock.organisasjonEnhetV1();
    }

    private OrganisasjonEnhetV1 lagEndpoint() {
        return new CXFClient<>(OrganisasjonEnhetV1.class)
                .address(System.getProperty("norg2.organisasjonenhet.v1.url"))
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .build();
    }

}
