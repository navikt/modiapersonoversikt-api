package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhetkontaktinformasjon;

import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.HentKontaktinformasjonForEnhetBolkUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.informasjon.Organisasjonsenhet;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.HentKontaktinformasjonForEnhetBolkRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.HentKontaktinformasjonForEnhetBolkResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhetkontaktinformasjon.OrganisasjonEnhetKontaktinformasjonV1EndpointCacheTest.ENHETSNAVN;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhetkontaktinformasjon.OrganisasjonEnhetKontaktinformasjonV1EndpointCacheTest.ENHETSNAVN_OPPSLAG_2;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class Config {

    @Bean
    public OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
        String key = "organisasjonEnhetKontaktinformasjonV1-key";
        System.setProperty(key, "true");
        OrganisasjonEnhetKontaktinformasjonV1 mock = mock(OrganisasjonEnhetKontaktinformasjonV1.class);
        when(mock.hentKontaktinformasjonForEnhetBolk(any(HentKontaktinformasjonForEnhetBolkRequest.class)))
                .thenReturn(mockRespons(ENHETSNAVN))
                .thenReturn(mockRespons(ENHETSNAVN_OPPSLAG_2));

        return mock;
    }

    private HentKontaktinformasjonForEnhetBolkResponse mockRespons(String enhetsnavn) {
        HentKontaktinformasjonForEnhetBolkResponse response = new HentKontaktinformasjonForEnhetBolkResponse();
        Organisasjonsenhet organiasjonenhet = new Organisasjonsenhet();
        organiasjonenhet.setEnhetNavn(enhetsnavn);
        response.getEnhetListe().add(organiasjonenhet);
        return response;
    }

}
