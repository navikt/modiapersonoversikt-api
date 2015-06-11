package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArbeidOgAktivitetEndpointMock {
    @Bean
    public static ArbeidOgAktivitet createArbeidOgAktivitetMock() {
        return new ArbeidOgAktivitet() {
            @Override
            public WSHentSakListeResponse hentSakListe(WSHentSakListeRequest wsHentSakListeRequest) {
                return new WSHentSakListeResponse();
            }
        };
    }
}
