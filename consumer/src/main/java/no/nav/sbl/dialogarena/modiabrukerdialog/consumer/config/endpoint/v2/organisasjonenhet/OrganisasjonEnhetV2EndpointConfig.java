package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhet;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.OrganisasjonEnhetV2Mock;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.binding.OrganisasjonEnhetV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class OrganisasjonEnhetV2EndpointConfig {

    public static final String NORG2_ORGANISASJON_ENHET_V2_KEY = "start.organisasjonenhet.v2.withmock";

    @Bean
    public OrganisasjonEnhetV2 organisasjonEnhetV2() {
        final OrganisasjonEnhetV2 organisasjonEnhetV2 = lagEndpoint().configureStsForSystemUserInFSS().build();
        final OrganisasjonEnhetV2 organisasjonEnhetV2Mock = lagMockEnpoint();

        return createMetricsProxyWithInstanceSwitcher("organisasjonEnhetV2", organisasjonEnhetV2,
                organisasjonEnhetV2Mock, NORG2_ORGANISASJON_ENHET_V2_KEY, OrganisasjonEnhetV2.class);
    }

    @Bean
    public Pingable gsakOrganisasjonEnhetPing() {
        return new PingableWebService("NORG2 - OrganisasjonEnhetV2",
                lagEndpoint().configureStsForSystemUserInFSS().build());
    }

    private OrganisasjonEnhetV2 lagMockEnpoint() {
        return OrganisasjonEnhetV2Mock.organisasjonEnhetV2();
    }

    private CXFClient<OrganisasjonEnhetV2> lagEndpoint() {
        return new CXFClient<>(OrganisasjonEnhetV2.class)
                .address(EnvironmentUtils.getRequiredProperty("norg2.organisasjonenhet.v2.url"));
    }

}
