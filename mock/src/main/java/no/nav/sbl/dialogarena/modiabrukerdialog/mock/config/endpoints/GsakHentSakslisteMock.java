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
                        createGenerellSak("111111111", "Arbeidsavklaring", "Fagsystem 1", "Generell", DateTime.now().minusDays(1)),
                        createGenerellSak("222222222", "Foreldrepenger", "Fagsystem 2", "Generell", DateTime.now().minusDays(4)),
                        createGenerellSak("333333333", "Hjelpemiddel", "Fagsystem 3", "Generell", DateTime.now().minusDays(4)),
                        createGenerellSak("444444444", "Bilsøknad", "Fagsystem 2", "Bilsøknad", DateTime.now().minusDays(4)),
                        createGenerellSak("555555555", "Annet", "Fagsystem 2", "Annet", DateTime.now().minusDays(4)),
                        createGenerellSak("666666666", "Dagpenger", "Fagsystem 1", "Dagpenger", DateTime.now().minusDays(4)));
            }
        };
    }

    private static WSGenerellSak createGenerellSak(String saksId, String tema, String fagsystem, String sakstype, DateTime opprettet) {
        return new WSGenerellSak()
                .withSakId(saksId)
                .withFagomradeKode(tema)
                .withEndringsinfo(new WSEndringsinfo().withOpprettetDato(opprettet))
                .withSakstypeKode(sakstype)
                .withFagsystemKode(fagsystem);
    }
}
