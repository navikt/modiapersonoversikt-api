package no.nav.sbl.dialogarena.sak.config;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
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
    public AktoerPortType fodselnummerAktorService() {
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
    public HenvendelseSoknaderPortType henvendelseSoknaderPortType() {
        return mock(HenvendelseSoknaderPortType.class);
    }

    @Bean
    public KodeverkClient kodeverkClient() {
        return mock(KodeverkClient.class);
    }

    @Bean
    public CmsContentRetriever cmsContentRetriever() {
        CmsContentRetriever cmsMock = new CmsContentRetriever() {
            @Override
            public String hentTekst(String key) {
                switch(key) {
                    case "mange.saker":
                        return "%d flere saker.";
                    case "ingen.saker":
                        return "finnes ikke noen saker";
                    case "saker.feilet":
                        return "kan ikke vise saker'";
                    default:
                        return "default tekst fra CMS-mock";
                }
            }
        };
        return cmsMock;
    }

}
