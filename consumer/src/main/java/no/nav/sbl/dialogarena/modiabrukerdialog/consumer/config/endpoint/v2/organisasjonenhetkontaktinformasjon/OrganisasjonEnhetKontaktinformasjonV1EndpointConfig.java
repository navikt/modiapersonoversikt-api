package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhetkontaktinformasjon;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.modig.modia.ping.UnpingableWebService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.FeatureToggle;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.OrganisasjonEnhetKontaktinformasjonV1Mock;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.OrganisasjonEnhetKontaktinformasjonV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class OrganisasjonEnhetKontaktinformasjonV1EndpointConfig {

    public static final String NORG2_ORGANISASJON_ENHET_KONTAKTINFORMASJON_V1_KEY = "start.organisasjonenhetkontaktinformasjon.v1.withmock";

    @Bean
    public OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1() {
        final OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1 = lagEndpoint()
                .configureStsForOnBehalfOfWithJWT()
                .build();
        final OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1Mock = lagMockEnpoint();

        return createMetricsProxyWithInstanceSwitcher("organisasjonEnhetKontaktinformasjonV1", organisasjonEnhetKontaktinformasjonV1,
                organisasjonEnhetKontaktinformasjonV1Mock, NORG2_ORGANISASJON_ENHET_KONTAKTINFORMASJON_V1_KEY, OrganisasjonEnhetKontaktinformasjonV1.class);
    }

    @Bean
    public Pingable OrganisasjonEnhetKontaktinformasjonPing() {
        return new PingableWebService(
                "NORG2 - OrganisasjonEnhetKontaktinformasjonV1",
                lagEndpoint().configureStsForSystemUserInFSS().build()
        );

    }

    private OrganisasjonEnhetKontaktinformasjonV1 lagMockEnpoint() {
        return OrganisasjonEnhetKontaktinformasjonV1Mock.organisasjonEnhetKontaktinformasjonV1();
    }

    private CXFClient<OrganisasjonEnhetKontaktinformasjonV1> lagEndpoint() {
        return new CXFClient<>(OrganisasjonEnhetKontaktinformasjonV1.class)
                .address(System.getProperty("norg2.organisasjonenhetkontaktinformasjon.v1.url"));
    }

}
