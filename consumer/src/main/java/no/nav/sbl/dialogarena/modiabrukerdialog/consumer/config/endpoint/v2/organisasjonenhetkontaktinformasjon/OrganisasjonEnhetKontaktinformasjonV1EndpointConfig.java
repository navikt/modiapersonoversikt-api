package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhetkontaktinformasjon;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.OrganisasjonEnhetKontaktinformasjonV1Mock;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.OrganisasjonEnhetKontaktinformasjonV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class OrganisasjonEnhetKontaktinformasjonV1EndpointConfig {

    @Bean
    public OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1() {
        final OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1 = lagEndpoint()
                .configureStsForSubject()
                .build();

        return createTimerProxyForWebService("organisasjonEnhetKontaktinformasjonV1", organisasjonEnhetKontaktinformasjonV1, OrganisasjonEnhetKontaktinformasjonV1.class);
    }

    @Bean
    public Pingable OrganisasjonEnhetKontaktinformasjonPing() {
        return new PingableWebService(
                "NORG2 - OrganisasjonEnhetKontaktinformasjonV1",
                lagEndpoint().configureStsForSystemUser().build()
        );

    }

    private OrganisasjonEnhetKontaktinformasjonV1 lagMockEnpoint() {
        return OrganisasjonEnhetKontaktinformasjonV1Mock.organisasjonEnhetKontaktinformasjonV1();
    }

    private CXFClient<OrganisasjonEnhetKontaktinformasjonV1> lagEndpoint() {
        return new CXFClient<>(OrganisasjonEnhetKontaktinformasjonV1.class)
                .address(System.getProperty("VIRKSOMHET_ORGANISASJONENHETKONTAKTINFORMASJON_V1_ENDPOINTURL"));
    }

}
