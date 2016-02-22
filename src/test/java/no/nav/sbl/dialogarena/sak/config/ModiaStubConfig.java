package no.nav.sbl.dialogarena.sak.config;

import no.nav.modig.content.ContentRetriever;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.saksoversikt.service.service.GsakSakerService;
import no.nav.sbl.dialogarena.saksoversikt.service.service.HenvendelseService;
import no.nav.sbl.dialogarena.saksoversikt.service.service.SakOgBehandlingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.journal.v2.Journal_v2PortType;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandling_v1PortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import org.springframework.context.annotation.Bean;

import static no.nav.sbl.dialogarena.sak.mock.SakOgBehandlingMocks.createWSSak;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Denne konfigen emulerer MODIA sin og tilbyr
 * stubber av de avhengighetene som kommer derfra
 */
public class ModiaStubConfig {

    @Bean
    public AktoerPortType aktoerPortType() {
        return mock(AktoerPortType.class);
    }

    @Bean
    public SakOgBehandling_v1PortType sakOgBehandlingPortType() {
        SakOgBehandling_v1PortType mock = mock(SakOgBehandling_v1PortType.class);
        when(mock.finnSakOgBehandlingskjedeListe(any(FinnSakOgBehandlingskjedeListeRequest.class)))
                .thenReturn(new FinnSakOgBehandlingskjedeListeResponse().withSak(createWSSak()));
        return mock;
    }

    @Bean
    public ContentRetriever contentRetriever() {
        return mock(ContentRetriever.class);
    }

    @Bean
    public HenvendelseSoknaderPortType henvendelseSoknaderPortType() {
        return mock(HenvendelseSoknaderPortType.class);
    }

    @Bean
    public Journal_v2PortType joarkV2() {
        return mock(Journal_v2PortType.class);
    }

    @Bean
    public HenvendelseService henvendelseService() {
        return mock(HenvendelseService.class);
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
    public GsakSakerService gSakService() {
        return mock(GsakSakerService.class);
    }

    @Bean
    public SakV1 sakEndpoint() {
        return mock(SakV1.class);
    }

    @Bean
    public PensjonSakV1 pensjonSakV1() {
        return mock(PensjonSakV1.class);
    }

    @Bean(name = "pep")
    public EnforcementPoint pep() {
        return mock(EnforcementPoint.class);
    }

    @Bean
    public SaksbehandlerInnstillingerService saksbehandlerInnstillingerService() {
        return mock(SaksbehandlerInnstillingerService.class);
    }

}
