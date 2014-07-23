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
public class GsakHentSakslistePortTypeMock {

    private static final String SAKSID_1 = "11111111";
    private static final String SAKSID_2 = "22222222";
    private static final String SAKSID_3 = "33333333";
    private static final String SAKSID_4 = "44444444";
    private static final String SAKSID_5 = "555555555";
    private static final String SAKSID_6 = "666666666";
    private static final String SAKSID_7 = "777777777";
    private static final String SAKSID_8 = "888888888";

    @Bean
    public Sak sakMock() {
        return createGsakHentSakslisteMock();
    }

    public static Sak createGsakHentSakslisteMock() {
        return new Sak() {
            @Override
            public WSFinnGenerellSakListeResponse finnGenerellSakListe(WSFinnGenerellSakListeRequest wsFinnGenerellSakListeRequest) {
                return new WSFinnGenerellSakListeResponse().withSakListe(
                        createGenerellSak(SAKSID_1, "Arbeidsavklaring", "Fagsystem 1", "Generell", DateTime.now().minusDays(1)),
                        createGenerellSak(SAKSID_2, "Foreldrepenger", "Fagsystem 2", "Generell", DateTime.now().minusDays(4)),
                        createGenerellSak(SAKSID_3, "Hjelpemiddel", "Fagsystem 3", "Generell", DateTime.now().minusDays(4)),
                        createGenerellSak(SAKSID_4, "Hjelpemiddel", "Fagsystem 3", "Generell", DateTime.now().minusDays(3)),
                        createGenerellSak(SAKSID_5, "Oppfølging", "Fagsystem 3", "Generell", DateTime.now().minusDays(4)),
                        createGenerellSak(SAKSID_6, "Bilsøknad", "Fagsystem 2", "Bilsøknad", DateTime.now().minusDays(4)),
                        createGenerellSak(SAKSID_7, "Annet", "Fagsystem 2", "Annet", DateTime.now().minusDays(4)),
                        createGenerellSak(SAKSID_8, "Dagpenger", "Fagsystem 1", "Dagpenger", DateTime.now().minusDays(4)));
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
