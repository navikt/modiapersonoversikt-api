package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhetkontaktinformasjon;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.OrganisasjonEnhetV2Mock;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.binding.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.OrganisasjonEnhetKontaktinformasjonV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class OrganisasjonEnhetKontaktinformasjonV1EndpointConfig {

    public static final String NORG2_ORGANISASJON_ENHET_KONTAKTINFORMASJON_V1_KEY = "start.organisasjonenhetkontaktinformasjon.v1.withmock";

    @Bean
    public OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1() {
        final OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1 = lagEndpoint();
        final OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1Mock = lagMockEnpoint();

        return createMetricsProxyWithInstanceSwitcher("organisasjonEnhetKontaktinformasjonV1", organisasjonEnhetKontaktinformasjonV1,
                organisasjonEnhetKontaktinformasjonV1Mock, NORG2_ORGANISASJON_ENHET_KONTAKTINFORMASJON_V1_KEY, OrganisasjonEnhetKontaktinformasjonV1.class);
    }

    @Bean
    public Pingable OrganisasjonEnhetKontaktinformasjonPing() {
        return new PingableWebService("NORG2 - OrganisasjonEnhetKontaktinformasjonV1", lagEndpoint());
    }

    private OrganisasjonEnhetKontaktinformasjonV1 lagMockEnpoint() {
        return null;
    }

    private OrganisasjonEnhetKontaktinformasjonV1 lagEndpoint() {
        return new CXFClient<>(OrganisasjonEnhetKontaktinformasjonV1.class)
                .address(System.getProperty("norg2.organisasjonenhetkontaktinformasjon.v1.url"))
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .build();
    }

}
