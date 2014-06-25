package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.gjennomforing.sak.v1.WSEndringsinfo;
import no.nav.virksomhet.gjennomforing.sak.v1.WSGenerellSak;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeResponse;
import no.nav.virksomhet.tjenester.sak.v1.Sak;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsakHentSakslisteMock {

    @Bean
    public Sak sakMock() {
        return createGsakHentSakslisteMock();
    }

    public static Sak createGsakHentSakslisteMock() {
        return new Sak() {
            @Override
            public WSFinnGenerellSakListeResponse finnGenerellSakListe(WSFinnGenerellSakListeRequest wsFinnGenerellSakListeRequest) {
                return new WSFinnGenerellSakListeResponse().withSakListe(
                        createGenerellSak("71972389639", "Dagpenger", "Arena", DateTime.now().minusDays(1)),
                        createGenerellSak("71972312343", "Arbeidsavklaring", "V2", DateTime.now().minusDays(4)),
                        createGenerellSak("71972338389", "Individstønad", "Infotrygd", DateTime.now().minusDays(4)));
            }
        };
    }

    private static WSGenerellSak createGenerellSak(String saksId, String tema, String fagsystem, DateTime opprettet) {
        return new WSGenerellSak()
                .withSakId(saksId)
                .withFagomradeKode(tema)
                .withEndringsinfo(new WSEndringsinfo().withOpprettetDato(opprettet))
                .withFagsystemKode(fagsystem);
    }
}
