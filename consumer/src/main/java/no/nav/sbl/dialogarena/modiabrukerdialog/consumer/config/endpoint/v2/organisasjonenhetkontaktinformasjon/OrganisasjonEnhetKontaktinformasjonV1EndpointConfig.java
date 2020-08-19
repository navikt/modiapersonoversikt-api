package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhetkontaktinformasjon;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.OrganisasjonEnhetKontaktinformasjonV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class OrganisasjonEnhetKontaktinformasjonV1EndpointConfig {
    @Autowired
    private StsConfig stsConfig;

    @Bean
    public OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1() {
        final OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1 = lagEndpoint()
                .configureStsForSubject(stsConfig)
                .build();

        return createTimerProxyForWebService("organisasjonEnhetKontaktinformasjonV1", organisasjonEnhetKontaktinformasjonV1, OrganisasjonEnhetKontaktinformasjonV1.class);
    }

    @Bean
    public Pingable OrganisasjonEnhetKontaktinformasjonPing() {
        return new PingableWebService(
                "NORG2 - OrganisasjonEnhetKontaktinformasjonV1",
                lagEndpoint().configureStsForSystemUser(stsConfig).build()
        );

    }

    private CXFClient<OrganisasjonEnhetKontaktinformasjonV1> lagEndpoint() {
        return new CXFClient<>(OrganisasjonEnhetKontaktinformasjonV1.class)
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_ORGANISASJONENHETKONTAKTINFORMASJON_V1_ENDPOINTURL"));
    }

}
