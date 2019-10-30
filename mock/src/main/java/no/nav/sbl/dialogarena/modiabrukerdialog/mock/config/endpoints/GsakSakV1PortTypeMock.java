package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.sak.v1.FinnSakForMangeForekomster;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakUgyldigInput;
import no.nav.tjeneste.virksomhet.sak.v1.HentSakSakIkkeFunnet;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakRequest;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakResponse;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSHentSakRequest;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSHentSakResponse;
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
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.AktoerPortTypeMock.AKTOER_ID_AREMARK_TESTFAMILIEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class GsakSakV1PortTypeMock {

    public static final String SAK_MED_INNSENDER = "15818532";
    public static final String SAK_UTEN_INNSENDER = "85154832";
    public static final String SAKSTYPE_GENERELL = "GEN";
    private static final WSPerson AKTOR = new WSPerson().withIdent(AKTOER_ID_AREMARK_TESTFAMILIEN);
    public static final String AKTORID_MOSS_TESTFAMILIEN = "1000096233937";
    private static final WSPerson ANNEN_INNSENDER = new WSPerson().withIdent(AKTORID_MOSS_TESTFAMILIEN);
    private static Random idGenerator = new Random();
    private static List<WSSak> defaultSaksliste = asList(
            createSak("DAG", DateTime.now().minusDays(1)),
            createSak("TRY", "FS22", SAKSTYPE_GENERELL, SAK_MED_INNSENDER, DateTime.now().minusDays(4)),
            createSak("HJE", "IT01", DateTime.now().minusDays(4)),
            createSak("FUL", "FS22", SAKSTYPE_GENERELL, SAK_UTEN_INNSENDER, DateTime.now().minusDays(3)),
            createSak("OPP", "AO01", SAKSTYPE_GENERELL, DateTime.now().minusDays(4)),
            createSak("BIL", "V2", "Bilsøknad", DateTime.now().minusDays(4)),
            createSak("IND", "OEBS", "Individstønad", DateTime.now().minusDays(4)),
            createSak("DAG", "PP01", "Dagpenger", DateTime.now().minusDays(4)),
            createSak("DAG", "AO11", "Dagpenger", DateTime.now().minusDays(2)),
            createSak("KLA", "AO11", "Klage/Anke", DateTime.now().minusDays(5)));

    private static List<WSSak> saksliste2 = asList(
            createSak("SYM", DateTime.now().minusDays(4)),
            createSak("DAG", "FS22", "Dagpenger", DateTime.now().minusDays(20)),
            createSak("DAG", "FS22", "Dagpenger", DateTime.now().minusDays(5)),
            createSak("DAG", "FS22", "Dagpenger", DateTime.now().minusDays(10)),
            createSak("FOR", "FS22", "Foreldrepenger", DateTime.now().minusDays(1)),
            createSak("FOR", "FS22", "Foreldrepenger", DateTime.now().minusDays(10)));

    private static List<WSSak> saksliste3 = asList(createSak("SYM", DateTime.now().minusWeeks(3)));
    private static WSSak defaultSak = createSak("DAG", DateTime.now().minusDays(5), AKTOR);

    private static Map<String, List<WSSak>> sakslisteMap =
            asMap(
                    "22222222222", saksliste2,
                    "11111111111", saksliste3);

    private static Map<String, WSSak> sakMap =
            asMap(
                    SAK_UTEN_INNSENDER, createSak("DAG", DateTime.now().minusDays(5), ANNEN_INNSENDER)
            );


    @Bean
    public SakV1 sakV1Mock() {
        return createGsakSakV1Mock();
    }

    public static SakV1 createGsakSakV1Mock() {
        try {
            SakV1 sakV1 = mock(SakV1.class);
            when(sakV1.finnSak(any(WSFinnSakRequest.class))).thenAnswer(new Answer<WSFinnSakResponse>() {
                @Override
                public WSFinnSakResponse answer(InvocationOnMock invocation) {
                    String bruker = ((WSFinnSakRequest) invocation.getArguments()[0]).getBruker().getIdent();
                    return new WSFinnSakResponse().withSakListe(sakerForBruker(bruker));
                }
            });
            when(sakV1.hentSak(any(WSHentSakRequest.class))).thenAnswer(new Answer<WSHentSakResponse>() {
                @Override
                public WSHentSakResponse answer(InvocationOnMock invocation) {
                    String sakId = ((WSHentSakRequest) invocation.getArguments()[0]).getSakId();
                    return new WSHentSakResponse().withSak(sakForSakId(sakId));
                }
            });
            return sakV1;
        } catch (FinnSakUgyldigInput | FinnSakForMangeForekomster | HentSakSakIkkeFunnet e) {
            throw new RuntimeException(e);
        }
    }

    private static WSSak sakForSakId(String sakId) {
        if (sakMap.containsKey(sakId)) {
            return sakMap.get(sakId);
        } else {
            return defaultSak;
        }
    }

    private static List<WSSak> sakerForBruker(String fnr) {
        if (sakslisteMap.containsKey(fnr)) {
            return sakslisteMap.get(fnr);
        } else {
            return defaultSaksliste;
        }
    }

    private static WSSak createSak(String tema, DateTime opprettet, WSPerson... brukerliste) {
        return new WSSak()
                .withSakId("saksid" + idGenerator.nextInt(100000000))
                .withGjelderBrukerListe(brukerliste)
                .withFagsystemSakId("fagsaksid" + idGenerator.nextInt(100000000))
                .withFagomraade(new WSFagomraader().withValue(tema))
                .withOpprettelsetidspunkt(opprettet)
                .withSakstype(new WSSakstyper().withValue(SAKSTYPE_GENERELL))
                .withFagsystem(new WSFagsystemer().withValue("FS22"));
    }

    private static WSSak createSak(String tema, String fagsystem, DateTime opprettet) {
        return createSak(tema, opprettet, AKTOR).withFagsystem(new WSFagsystemer().withValue(fagsystem));
    }

    private static WSSak createSak(String tema, String fagsystem, String sakstype, DateTime opprettet) {
        return createSak(tema, fagsystem, opprettet).withSakstype(new WSSakstyper().withValue(sakstype));
    }

    private static WSSak createSak(String tema, String fagsystem, String sakstype, String saksId, DateTime opprettet) {
        return createSak(tema, fagsystem, sakstype, opprettet).withSakId(saksId);
    }

}
