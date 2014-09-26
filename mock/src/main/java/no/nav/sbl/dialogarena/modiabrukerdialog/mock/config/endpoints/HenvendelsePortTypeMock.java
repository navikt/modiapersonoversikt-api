package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseResponse;
import org.joda.time.DateTime;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT_OPPMOTE;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL_SKRIFTLIG;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_SKRIFTLIG;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class HenvendelsePortTypeMock {

    private static Random idGenerator = new Random();
    private static int oppgaveId = 0;

    private static final String FNR = "11111111111";
    private static final String NAVIDENT = "Z999999";

    private static final String JOURNALFORT_SAKSID_FORELDREPENGER = GsakHentSakslistePortTypeMock.SAKSID_1;
    private static final String JOURNALFORT_SAKSID_HJELPEMIDLER = GsakHentSakslistePortTypeMock.SAKSID_2;
    private static final String JOURNALFORER_NAV_IDENT = "567567567";

    private static final String LANG_TEKST = "Lorem ipsum dolor sit amet, " +
            "consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad " +
            "minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure " +
            "dolor in hendrerit in http://www.nav.no vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
            " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis" +
            " eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis" +
            " in iis qui facit eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus" +
            " dynamicus, qui sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit" +
            " litterarum formas humanitatis per seacula quarta decima et quinta decima. Eodem modo typi, qui nunc nobis videntur parum clari, fiant sollemnes" +
            " in futurum.";

    private static final String KORT_TEKST = "Lorem ipsum dolor sit amet, " +
            "consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad " +
            "minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure " +
            "dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto";

    private static String behandlingsId1 = randomId();
    private static String behandlingsId2 = randomId();
    private static String behandlingsId3 = randomId();
    private static String behandlingsId4 = randomId();
    private static String behandlingsId5 = randomId();

    private static String randomId() {
        return valueOf(idGenerator.nextInt());
    }

    public static final List<XMLHenvendelse> HENVENDELSER = new ArrayList<>(asList(
            createXMLHenvendelse(SPORSMAL_SKRIFTLIG, behandlingsId1, behandlingsId1, now().minusWeeks(1), null,
                    createXMLMeldingFraBruker("FMLI", LANG_TEKST), valueOf(oppgaveId), now().minusDays(2), "FOR", JOURNALFORT_SAKSID_FORELDREPENGER, JOURNALFORER_NAV_IDENT),

            createXMLHenvendelse(SVAR_SKRIFTLIG, randomId(), behandlingsId1, now().minusDays(2), now().minusDays(4),
                    createXMLMeldingTilBruker("FMLI", "TELEFON", "Vi kan bekrefte at du får foreldrepenger"), null, null, "", "", ""),

            createXMLHenvendelse(SVAR_SKRIFTLIG, randomId(), behandlingsId1, now().minusDays(3), now().minusDays(4),
                    createXMLMeldingTilBruker("FMLI", "TELEFON", "Det er meget sannsynlig at du kan få foreldrepenger"),
                    null, now().minusDays(2), "FOR", JOURNALFORT_SAKSID_FORELDREPENGER, JOURNALFORER_NAV_IDENT),

            createXMLHenvendelse(SVAR_SKRIFTLIG, randomId(), behandlingsId1, now().minusDays(5), now().minusDays(5),
                    createXMLMeldingTilBruker("FMLI", "TELEFON", "Det kan hende at du kan få foredrepenger "),
                    null, now().minusDays(3), "FOR", JOURNALFORT_SAKSID_FORELDREPENGER, JOURNALFORER_NAV_IDENT),

            createXMLHenvendelse(SVAR_SKRIFTLIG, randomId(), behandlingsId1, now().minusDays(5), now().minusDays(6),
                    createXMLMeldingTilBruker("FMLI", "TELEFON", "Vi har hatt en samtale og det kommer frem at Test Testesen ønsker foreldrepenger "),
                    null, now().minusDays(3), "FOR", JOURNALFORT_SAKSID_FORELDREPENGER, JOURNALFORER_NAV_IDENT),

            createXMLHenvendelse(SPORSMAL_SKRIFTLIG, behandlingsId2, behandlingsId2, now().minusDays(3), null,
                    createXMLMeldingFraBruker("ARBD", LANG_TEKST),
                    valueOf(oppgaveId++), now().minusDays(1), "SYK", JOURNALFORT_SAKSID_HJELPEMIDLER, JOURNALFORER_NAV_IDENT),

            createXMLHenvendelse(SVAR_SKRIFTLIG, randomId(), behandlingsId2, now().minusHours(5), null,
                    createXMLMeldingTilBruker("ARBD", "TEKST", KORT_TEKST),
                    null, now().minusDays(1), "SYK", JOURNALFORT_SAKSID_HJELPEMIDLER, JOURNALFORER_NAV_IDENT),

            createXMLHenvendelse(SPORSMAL_SKRIFTLIG, behandlingsId3, behandlingsId3, now().minusMonths(4), null,
                    createXMLMeldingFraBruker("ARBD", LANG_TEKST), valueOf(oppgaveId++), null, "", "", ""),

            createXMLHenvendelse(REFERAT_OPPMOTE, behandlingsId4, behandlingsId4, now(), null,
                    createXMLMeldingTilBruker("ARBD", "TELEFON", "Test Testesen er utålmodig på å få utbetalt dagpengene sine"), null, null, "", "", ""),

            createXMLHenvendelse(SPORSMAL_SKRIFTLIG, behandlingsId5, behandlingsId5, now().minusDays(1), null,
                    null, valueOf(oppgaveId++), null, "", "", "")
    ));

    private static XMLHenvendelse createXMLHenvendelse(XMLHenvendelseType type, String behandlingsId, String behandlingskjedeId, DateTime opprettet, DateTime lestDato, XMLMetadata metadata,
                                                       String oppgaveId, DateTime journalfortDato, String journalfortTema, String journalfortSaksId, String journalforerNavIdent) {

        XMLHenvendelse xmlHenvendelse = new XMLHenvendelse()
                .withHenvendelseType(type.name())
                .withBehandlingsId(behandlingsId)
                .withBehandlingskjedeId(behandlingskjedeId)
                .withOpprettetDato(opprettet)
                .withLestDato(lestDato)
                .withFnr(FNR)
                .withJournalfortInformasjon(
                        new XMLJournalfortInformasjon()
                                .withJournalfortDato(journalfortDato)
                                .withJournalfortTema(journalfortTema)
                                .withJournalfortSaksId(journalfortSaksId)
                                .withJournalforerNavIdent(journalforerNavIdent)
                )
                .withOppgaveIdGsak(oppgaveId);

        if (metadata == null) {
            return xmlHenvendelse.withMetadataListe(null);
        } else {
            return xmlHenvendelse.withMetadataListe(
                    new XMLMetadataListe().withMetadata(metadata));
        }
    }

    private static XMLMeldingFraBruker createXMLMeldingFraBruker(String temagruppe, String tekst) {
        return new XMLMeldingFraBruker().withTemagruppe(temagruppe).withFritekst(tekst);
    }

    private static XMLMeldingTilBruker createXMLMeldingTilBruker(String temagruppe, String kanal, String fritekst) {
        return new XMLMeldingTilBruker().withTemagruppe(temagruppe).withKanal(kanal).withFritekst(fritekst).withNavident(NAVIDENT);
    }

    private static XMLHenvendelse hentHenvendelseMedBehandlingsId(WSHentHenvendelseRequest req) {
        String behandlingId = req == null ? "" : req.getBehandlingsId();
        XMLHenvendelse henvendelse = new XMLHenvendelse();
        for (XMLHenvendelse xmlHenvendelse : HENVENDELSER) {
            if (xmlHenvendelse.getBehandlingsId().equals(behandlingId)) {
                henvendelse = xmlHenvendelse;
            }
        }
        return henvendelse;
    }

    @Bean
    public HenvendelsePortType henvendelsePortType() {
        return createHenvendelsePortTypeMock();
    }

    public static HenvendelsePortType createHenvendelsePortTypeMock() {
        HenvendelsePortType mockI = mock(HenvendelsePortType.class);
        when(mockI.hentHenvendelse(any(WSHentHenvendelseRequest.class))).thenAnswer(new Answer<WSHentHenvendelseResponse>() {
            @Override
            public WSHentHenvendelseResponse answer(InvocationOnMock invocation) throws Throwable {
                WSHentHenvendelseRequest req = (WSHentHenvendelseRequest) invocation.getArguments()[0];
                return new WSHentHenvendelseResponse().withAny(hentHenvendelseMedBehandlingsId(req));
            }
        });
        when(mockI.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(
                new WSHentHenvendelseListeResponse().withAny(HENVENDELSER.toArray())
        );
        return mockI;
    }
}
