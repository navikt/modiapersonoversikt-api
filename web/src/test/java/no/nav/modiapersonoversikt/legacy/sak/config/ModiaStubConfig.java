package no.nav.modiapersonoversikt.legacy.sak.config;

import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService;
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService;
import no.nav.modiapersonoversikt.service.saker.SakerService;
import no.nav.modiapersonoversikt.legacy.sak.service.SakOgBehandlingService;
import no.nav.modiapersonoversikt.service.unleash.UnleashService;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import org.springframework.context.annotation.Bean;

import static no.nav.modiapersonoversikt.legacy.sak.mock.SakOgBehandlingMocks.createWSSak;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Denne konfigen emulerer MODIA sin og tilbyr
 * stubber av de avhengighetene som kommer derfra
 */
public class ModiaStubConfig {
    @Bean
    public SakOgBehandlingV1 sakOgBehandlingPortType() throws Exception {
        SakOgBehandlingV1 mock = mock(SakOgBehandlingV1.class);
        FinnSakOgBehandlingskjedeListeResponse response = new FinnSakOgBehandlingskjedeListeResponse();
        response.getSak().add(createWSSak());
        when(mock.finnSakOgBehandlingskjedeListe(any(FinnSakOgBehandlingskjedeListeRequest.class))).thenReturn(response);
        return mock;
    }

    @Bean
    public SakOgBehandlingService sakOgBehandlingService() {
        return mock(SakOgBehandlingService.class);
    }

    @Bean
    public AnsattService ansattService() {
        return mock(AnsattService.class);
    }

    @Bean
    public Tilgangskontroll tilgangskontroll() {
        return mock(Tilgangskontroll.class);
    }

    @Bean
    public PdlOppslagService pdlOppslagService() { return mock(PdlOppslagService.class); }

    @Bean
    public SakerService sakerService() { return mock(SakerService.class); }

    @Bean
    public UnleashService unleashService() { return mock(UnleashService.class); }

}
