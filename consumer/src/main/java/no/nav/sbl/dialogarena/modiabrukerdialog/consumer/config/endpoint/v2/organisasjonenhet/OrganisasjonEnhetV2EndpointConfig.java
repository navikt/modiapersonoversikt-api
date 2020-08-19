package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhet;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class OrganisasjonEnhetV2EndpointConfig {
    @Inject
    private StsConfig stsConfig;

    @Bean
    public OrganisasjonEnhetV2 organisasjonEnhetV2() {
        final OrganisasjonEnhetV2 organisasjonEnhetV2 = lagEndpoint().configureStsForSystemUser(stsConfig).build();

        return createTimerProxyForWebService("organisasjonEnhetV2", organisasjonEnhetV2, OrganisasjonEnhetV2.class);
    }

    @Bean
    public Pingable gsakOrganisasjonEnhetPing() {
        return new PingableWebService("NORG2 - OrganisasjonEnhetV2",
                lagEndpoint().configureStsForSystemUser(stsConfig).build());
    }

    private CXFClient<OrganisasjonEnhetV2> lagEndpoint() {
        return new CXFClient<>(OrganisasjonEnhetV2.class)
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_ORGANISASJONENHET_V2_ENDPOINTURL"));
    }

}
