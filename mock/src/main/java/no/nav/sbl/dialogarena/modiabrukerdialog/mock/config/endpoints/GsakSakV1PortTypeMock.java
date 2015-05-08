package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.sak.v1.*;
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

import java.util.*;

import static no.nav.sbl.dialogarena.common.collections.Collections.asList;
import static no.nav.sbl.dialogarena.common.collections.Collections.asMap;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class GsakSakV1PortTypeMock {

    public static final String SAKSID_1 = "15818532";
    public static final String SAKSID_2 = "85154832";
    public static final String SAKSTYPE_GENERELL = "GEN";

    private static Random idGenerator = new Random();
    private static List<WSSak> defaultSaksliste = asList(
            createSak("DAG", DateTime.now().minusDays(1)),
            createSak("TRY", "FS22", SAKSTYPE_GENERELL, SAKSID_1, DateTime.now().minusDays(4)),
            createSak("HJE", "IT01", DateTime.now().minusDays(4)),
            createSak("FUL", "FS22", SAKSTYPE_GENERELL, SAKSID_2, DateTime.now().minusDays(3)),
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

    private static Map<String, List<WSSak>> sakslisteMap =
            asMap(
                    "11111111111", saksliste2,
                    "12345678901", saksliste3);

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
                    return new WSHentSakResponse().withSak(createSak("DAG", DateTime.now().minusDays(5)));
                }
            });
            return sakV1;
        } catch (FinnSakUgyldigInput | FinnSakForMangeForekomster | HentSakSakIkkeFunnet e) {
            throw new RuntimeException(e);
        }
    }

    private static List<WSSak> sakerForBruker(String fnr) {
        if (sakslisteMap.containsKey(fnr)) {
            return sakslisteMap.get(fnr);
        } else {
            return defaultSaksliste;
        }
    }

    private static WSSak createSak(String tema, DateTime opprettet) {
        return new WSSak()
                .withSakId("" + idGenerator.nextInt(100000000))
                .withFagsystemSakId("" + idGenerator.nextInt(100000000))
                .withFagomraade(new WSFagomraader().withValue(tema))
                .withOpprettelsetidspunkt(opprettet)
                .withSakstype(new WSSakstyper().withValue(SAKSTYPE_GENERELL))
                .withFagsystem(new WSFagsystemer().withValue("FS22"));
    }

    private static WSSak createSak(String tema, String fagsystem, DateTime opprettet) {
        return createSak(tema, opprettet).withFagsystem(new WSFagsystemer().withValue(fagsystem));
    }

    private static WSSak createSak(String tema, String fagsystem, String sakstype, DateTime opprettet) {
        return createSak(tema, fagsystem, opprettet).withSakstype(new WSSakstyper().withValue(sakstype));
    }

    private static WSSak createSak(String tema, String fagsystem, String sakstype, String saksId, DateTime opprettet) {
        return createSak(tema, fagsystem, sakstype, opprettet).withSakId(saksId);
    }

}
