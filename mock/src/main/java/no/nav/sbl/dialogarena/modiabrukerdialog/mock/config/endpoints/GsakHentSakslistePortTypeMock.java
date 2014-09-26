package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.gjennomforing.sak.v1.WSEndringsinfo;
import no.nav.virksomhet.gjennomforing.sak.v1.WSGenerellSak;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeResponse;
import no.nav.virksomhet.tjenester.sak.v1.Sak;
import org.joda.time.DateTime;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static no.nav.sbl.dialogarena.common.collections.Collections.asList;
import static no.nav.sbl.dialogarena.common.collections.Collections.asMap;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class GsakHentSakslistePortTypeMock {

    public static final String SAKSID_1 = "15818532";
    public static final String SAKSID_2 = "85154832";
    public static final String SAKSTYPE_GENERELL = "GEN";

    private static Random idGenerator = new Random();
    private static List<WSGenerellSak> defaultSaksliste = asList(
            createSak("DAG", DateTime.now().minusDays(1)),
            createSak("TRY", "FS22", SAKSTYPE_GENERELL, SAKSID_1, DateTime.now().minusDays(4)),
            createSak("HJE", "IT01", DateTime.now().minusDays(4)),
            createSak("FUL", "FS22", SAKSTYPE_GENERELL, SAKSID_2, DateTime.now().minusDays(3)),
            createSak("OPP", "AO01", SAKSTYPE_GENERELL, DateTime.now().minusDays(4)),
            createSak("BIL", "V2", "Bilsøknad", DateTime.now().minusDays(4)),
            createSak("IND", "OEBS", "Individstønad", DateTime.now().minusDays(4)),
            createSak("DAG", "PP01", "Dagpenger", DateTime.now().minusDays(4)),
            createSak("DAG", "AO11", "Dagpenger", DateTime.now().minusDays(2)));

    private static List<WSGenerellSak> saksliste2 = asList(
            createSak("SYM", DateTime.now().minusDays(4)),
            createSak("DAG", "FS22", "Dagpenger", DateTime.now().minusDays(20)),
            createSak("DAG", "FS22", "Dagpenger", DateTime.now().minusDays(5)),
            createSak("DAG", "FS22", "Dagpenger", DateTime.now().minusDays(10)),
            createSak("FOR", "FS22", "Foreldrepenger", DateTime.now().minusDays(1)),
            createSak("FOR", "FS22", "Foreldrepenger", DateTime.now().minusDays(10)));

    private static List<WSGenerellSak> saksliste3 = asList(createSak("SYM", DateTime.now().minusWeeks(3)));

    private static Map<String, List<WSGenerellSak>> sakslisteMap =
            asMap(
                    "11111111111", saksliste2,
                    "12345678901", saksliste3);

    @Bean
    public Sak sakMock() {
        return createGsakHentSakslisteMock();
    }

    public static Sak createGsakHentSakslisteMock() {
        Sak s = mock(Sak.class);
        when(s.finnGenerellSakListe(any(WSFinnGenerellSakListeRequest.class))).thenAnswer(new Answer<WSFinnGenerellSakListeResponse>() {
            @Override
            public WSFinnGenerellSakListeResponse answer(InvocationOnMock invocation) throws Throwable {
                String user = ((WSFinnGenerellSakListeRequest) invocation.getArguments()[0]).getBrukerId();
                return new WSFinnGenerellSakListeResponse().withSakListe(sakslisteForBruker(user));
            }
        });
        return s;
    }

    private static List<WSGenerellSak> sakslisteForBruker(String fnr) {
        if (sakslisteMap.containsKey(fnr)) {
            return sakslisteMap.get(fnr);
        } else {
            return defaultSaksliste;
        }
    }

    private static WSGenerellSak createSak(String tema, DateTime opprettet) {
        return new WSGenerellSak()
                .withSakId("" + idGenerator.nextInt(100000000))
                .withFagomradeKode(tema)
                .withEndringsinfo(new WSEndringsinfo().withOpprettetDato(opprettet))
                .withSakstypeKode(SAKSTYPE_GENERELL)
                .withFagsystemKode("FS22");
    }

    private static WSGenerellSak createSak(String tema, String fagsystem, DateTime opprettet) {
        return createSak(tema, opprettet).withFagsystemKode(fagsystem);
    }

    private static WSGenerellSak createSak(String tema, String fagsystem, String sakstype, DateTime opprettet) {
        return createSak(tema, fagsystem, opprettet).withSakstypeKode(sakstype);
    }

    private static WSGenerellSak createSak(String tema, String fagsystem, String sakstype, String saksId, DateTime opprettet) {
        return createSak(tema, fagsystem, sakstype, opprettet).withSakId(saksId);
    }

}
