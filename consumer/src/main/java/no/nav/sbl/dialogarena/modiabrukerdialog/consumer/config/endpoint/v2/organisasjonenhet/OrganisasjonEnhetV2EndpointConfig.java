package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhet;

import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class OrganisasjonEnhetV2EndpointConfig {

    @Bean
    public OrganisasjonEnhetV2 organisasjonEnhetV2() {
        final OrganisasjonEnhetV2 organisasjonEnhetV2 = lagEndpoint().configureStsForSystemUser().build();

        return createTimerProxyForWebService("organisasjonEnhetV2", organisasjonEnhetV2, OrganisasjonEnhetV2.class);
    }

    @Bean
    public Pingable gsakOrganisasjonEnhetPing() {
        return new PingableWebService("NORG2 - OrganisasjonEnhetV2",
                lagEndpoint().configureStsForSystemUser().build());
    }

    private CXFClient<OrganisasjonEnhetV2> lagEndpoint() {
        return new CXFClient<>(OrganisasjonEnhetV2.class)
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_ORGANISASJONENHET_V2_ENDPOINTURL"));
    }

}
