package no.nav.modiapersonoversikt.legacy.sak.config;

import no.nav.modiapersonoversikt.consumer.kodeverk2.KodeverkClient;
import no.nav.modiapersonoversikt.infrastructure.content.ContentRetriever;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock;
import no.nav.modiapersonoversikt.legacy.api.service.FodselnummerAktorService;
import no.nav.modiapersonoversikt.legacy.api.service.norg.AnsattService;
import no.nav.modiapersonoversikt.legacy.api.service.saker.SakerService;
import no.nav.modiapersonoversikt.legacy.sak.service.SakOgBehandlingService;
import no.nav.modiapersonoversikt.service.unleash.UnleashService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.virksomhet.aktoer.v2.Aktoer_v2;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.InnsynJournalV2;
import no.nav.tjeneste.virksomhet.journal.v2.JournalV2;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
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
    public Aktoer_v2 aktoerPortType() {
        return mock(Aktoer_v2.class);
    }

    @Bean
    public SakOgBehandlingV1 sakOgBehandlingPortType() throws Exception {
        SakOgBehandlingV1 mock = mock(SakOgBehandlingV1.class);
        FinnSakOgBehandlingskjedeListeResponse response = new FinnSakOgBehandlingskjedeListeResponse();
        response.getSak().add(createWSSak());
        when(mock.finnSakOgBehandlingskjedeListe(any(FinnSakOgBehandlingskjedeListeRequest.class))).thenReturn(response);
        return mock;
    }

    @Bean
    public HenvendelseSoknaderPortType henvendelseSoknaderPortType() {
        return mock(HenvendelseSoknaderPortType.class);
    }

    @Bean
    public JournalV2 joarkV2() {
        return mock(JournalV2.class);
    }

    @Bean
    public InnsynJournalV2 innsynJournalV2(){
        return mock(InnsynJournalV2.class);
    }

    @Bean
    public SakOgBehandlingService sakOgBehandlingService() {
        return mock(SakOgBehandlingService.class);
    }

    @Bean
    public KodeverkClient kodeverkClient() {
        return mock(KodeverkClient.class);
    }

    @Bean
    public PensjonSakV1 pensjonSakV1() {
        return mock(PensjonSakV1.class);
    }

    @Bean
    public AnsattService ansattService() {
        return mock(AnsattService.class);
    }

    @Bean
    public Tilgangskontroll tilgangskontroll() {
        return TilgangskontrollMock.get();
    }

    @Bean
    public ContentRetriever contentRetriever() {
        return mock(ContentRetriever.class);
    }

    @Bean
    public FodselnummerAktorService fodselnummerAktorService() { return mock(FodselnummerAktorService.class); }

    @Bean
    public SakerService sakerService() { return mock(SakerService.class); }

    @Bean
    public UnleashService unleashService() { return mock(UnleashService.class); }

}
