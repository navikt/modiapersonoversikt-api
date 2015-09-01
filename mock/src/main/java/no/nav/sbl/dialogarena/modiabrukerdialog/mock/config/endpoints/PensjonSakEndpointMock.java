package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.pensjonsak.v1.HentSakSammendragListePersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.HentSakSammendragListeSakManglerEierenhet;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentPensjonsinfoListeBolkRequest;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentPensjonsinfoListeBolkResponse;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentSakSammendragListeRequest;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentSakSammendragListeResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PensjonSakEndpointMock {

    @Bean
    public PensjonSakV1 pensjonSakV1() {
        return createPensjonSakV1Mock();
    }

    public static PensjonSakV1 createPensjonSakV1Mock() {
        return new PensjonSakV1() {
            @Override
            public WSHentSakSammendragListeResponse hentSakSammendragListe(WSHentSakSammendragListeRequest hentSakSammendragListeRequest) throws HentSakSammendragListeSakManglerEierenhet, HentSakSammendragListePersonIkkeFunnet {
                return new WSHentSakSammendragListeResponse();
            }

            @Override
            public WSHentPensjonsinfoListeBolkResponse hentPensjonsinfoListeBolk(WSHentPensjonsinfoListeBolkRequest hentPensjonsinfoListeBolkRequest) {
                throw new RuntimeException("Not implemented");
            }

            @Override
            public void ping() {
            }
        };
    }
}
