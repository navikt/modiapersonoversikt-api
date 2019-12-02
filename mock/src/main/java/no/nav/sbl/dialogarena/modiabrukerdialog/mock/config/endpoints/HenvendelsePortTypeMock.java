package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.*;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
import static org.joda.time.DateTime.now;

@Configuration
public class HenvendelsePortTypeMock {

    private static Random idGenerator = new Random();
    private static int oppgaveId = 0;

    private static final String FNR = "11111111111";
    private static final String NAVIDENT = "Z123456";

    private static final String JOURNALFORT_SAKSID_HJELPEMIDLER = GsakSakV1PortTypeMock.SAK_UTEN_INNSENDER;
    private static final String JOURNALFORER_NAV_IDENT = "567567567";

    private static final String LANG_TEKST = "Lorem ipsum dolor sit amet, " +
            "consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad " +
            "minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure " +
            "dolor in hendrerit in http://www.nav.no vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
            " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis" +
            " eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis" +
            " in iis qui facit eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus" +
            " dynamicus, qui http://stash.devillo.no/projects/DIAL/repos/modiabrukerdialog/browse/web/src/main/java/no/nav/sbl/dialogarena/modiabrukerdialog/web/WicketApplication.java?at=2108eaa21754c08ea0a7eaa6d5acacaf62513e92 sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit" +
            " litterarum formas humanitatis per seacula quarta decima et quinta decima. Eodem modo typi, qui nunc nobis videntur parum clari, fiant sollemnes" +
            " in futurum. link test bughunt \n" +
            "www.nav.no \n" +
            "http://nav.no \n" +
            "https://nav.no \n" +
            "https://tjenester-t4.nav.no/mininnboks/sporsmal/skriv/ARBD?0 \n" +
            "www.tjenester-t4.nav.no/mininnboks/sporsmal/skriv/ARBD?0 \n" +
            "www.nav.no?urlparam=param1&param2=parma2";

    private static final String KORT_TEKST = "Lorem ipsum dolor sit amet, " +
            "consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad " +
            "minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure " +
            "dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto";

    public static final String BEHANDLINGS_ID1 = randomId();
    public static final String BEHANDLINGS_ID2 = randomId();
    public static final String BEHANDLINGS_ID3 = randomId();
    public static final String BEHANDLINGS_ID4 = randomId();
    public static final String BEHANDLINGS_ID5 = randomId();
    public static final String BEHANDLINGS_ID6 = randomId();
    public static final String BEHANDLINGS_ID7 = randomId();
    public static final String BEHANDLINGS_ID8 = randomId();

    private static String randomId() {
        return valueOf(idGenerator.nextInt());
    }

    public static final List<XMLHenvendelse> HENVENDELSER = new ArrayList<>(asList(
            lagDokumentVarsel("Dagpenger", "Vedtaksbrev"),
            lagOppgaveVarsel(),

            createXMLHenvendelse(BEHANDLINGS_ID1, BEHANDLINGS_ID1, now().minusWeeks(1), null,
                    createXMLMeldingFraBruker("OVRG", LANG_TEKST), valueOf(oppgaveId), createXMLJourfortInformasjon(null, null, null, null))
                    .withHenvendelseType(SPORSMAL_SKRIFTLIG.name()),

            createXMLHenvendelse(randomId(), BEHANDLINGS_ID1, now().minusDays(2), now().minusDays(4),
                    createXMLMeldingTilBruker("OVRG", "TELEFON", "Vi kan bekrefte at du får foreldrepenger"), null, createXMLJourfortInformasjon(null, "", "", ""))
                    .withHenvendelseType(SVAR_SKRIFTLIG.name()),

            createXMLHenvendelse(randomId(), BEHANDLINGS_ID1, now().minusDays(3), now().minusDays(4),
                    createXMLMeldingTilBruker("OVRG", "TELEFON", "Det er meget sannsynlig at du kan få foreldrepenger"), null, createXMLJourfortInformasjon(null, "", "", ""))
                    .withHenvendelseType(SVAR_SKRIFTLIG.name()),

            createXMLHenvendelse(randomId(), BEHANDLINGS_ID1, now().minusDays(5), now().minusDays(5),
                    createXMLMeldingTilBruker("OVRG", "TELEFON", "Det kan hende at du kan få foredrepenger "), null, createXMLJourfortInformasjon(null, "", "", ""))
                    .withHenvendelseType(SVAR_SKRIFTLIG.name()),

            createXMLHenvendelse(randomId(), BEHANDLINGS_ID1, now().minusDays(5), now().minusDays(6),
                    createXMLMeldingTilBruker("OVRG", "TELEFON", "Vi har hatt en samtale og det kommer frem at Test Testesen ønsker foreldrepenger "), null, createXMLJourfortInformasjon(null, "", "", ""))
                    .withHenvendelseType(SVAR_SKRIFTLIG.name()),

            createXMLHenvendelse(BEHANDLINGS_ID2, BEHANDLINGS_ID2, now().minusDays(3), null,
                    createXMLMeldingFraBruker("PENS", LANG_TEKST),
                    valueOf(oppgaveId++), createXMLJourfortInformasjon(now().minusDays(1), "SYK", JOURNALFORT_SAKSID_HJELPEMIDLER, JOURNALFORER_NAV_IDENT))
                    .withHenvendelseType(SPORSMAL_SKRIFTLIG.name()),

            createXMLHenvendelse(randomId(), BEHANDLINGS_ID2, now().minusHours(5), null,
                    createXMLMeldingTilBruker("PENS", "TEKST", KORT_TEKST),
                    null, createXMLJourfortInformasjon(now().minusDays(1), "SYK", JOURNALFORT_SAKSID_HJELPEMIDLER, JOURNALFORER_NAV_IDENT))
                    .withHenvendelseType(SVAR_SKRIFTLIG.name()),

            createXMLHenvendelse(BEHANDLINGS_ID3, BEHANDLINGS_ID3, now().minusMinutes(5), null,
                    createXMLMeldingFraBruker("FMLI", LANG_TEKST), valueOf(oppgaveId++), createXMLJourfortInformasjon(null, "", "", ""))
                    .withHenvendelseType(SPORSMAL_SKRIFTLIG.name()),

            createXMLHenvendelse(BEHANDLINGS_ID4, BEHANDLINGS_ID4, now(), null,
                    createXMLMeldingTilBruker("ORT_HJE", "TEKST", KORT_TEKST), null, createXMLJourfortInformasjon(DateTime.now(), "DAG", "1234", "z999999"))
                    .withHenvendelseType(SPORSMAL_MODIA_UTGAAENDE.name())
                    .withFerdigstiltUtenSvar(true),

            createXMLHenvendelse(BEHANDLINGS_ID5, BEHANDLINGS_ID5, now().minusDays(1), null,
                    createXMLMeldingTilBruker("ARBD", "TEKST", KORT_TEKST), valueOf(oppgaveId++), createXMLJourfortInformasjon(null, "", "", ""))
                    .withHenvendelseType(SPORSMAL_SKRIFTLIG.name())
                    .withKontorsperreEnhet("0122")
                    .withBrukersEnhet("1234"),

            createXMLHenvendelse(BEHANDLINGS_ID6, BEHANDLINGS_ID6, now().minusDays(2), null,
                    null, valueOf(oppgaveId++), createXMLJourfortInformasjon(null, "", "", "")).withHenvendelseType(REFERAT_TELEFON.name()),

            createXMLHenvendelse(BEHANDLINGS_ID7, BEHANDLINGS_ID7, now().minusDays(4), null,
                    createXMLMeldingFraBruker("OKSOS", LANG_TEKST),
                    valueOf(oppgaveId++), createXMLJourfortInformasjon(null, "", "", ""))
                    .withHenvendelseType(SPORSMAL_SKRIFTLIG.name()),

            createXMLHenvendelse(randomId(), BEHANDLINGS_ID7, now().minusHours(12), null,
                    createXMLMeldingTilBruker("OKSOS", "TEKST", KORT_TEKST),
                    null, createXMLJourfortInformasjon(null, "", "", ""))
                    .withHenvendelseType(SVAR_SKRIFTLIG.name()),

            henvendelseMedGjeldendeTemagruppe(BEHANDLINGS_ID8, "ANSOS")

    ));

    private static XMLHenvendelse lagOppgaveVarsel() {
        String id = randomId();
        return new XMLHenvendelse()
                .withBehandlingsId(id)
                .withBehandlingskjedeId(id)
                .withOpprettetDato(DateTime.now().minusMinutes(3))
                .withKorrelasjonsId("a1-b4")
                .withHenvendelseType(OPPGAVE_VARSEL.value())
                .withMetadataListe(new XMLMetadataListe().withMetadata(new XMLOppgaveVarsel()
                        .withOppgaveType("SEND_SYKMELDING")
                        .withOppgaveURL("https://wwww.example.com")
                        .withTemagruppe("SYK")));
    }

    private static XMLHenvendelse henvendelseMedGjeldendeTemagruppe(String behandlingsId, String gjeldendeTemagruppe) {
        return createXMLHenvendelse(behandlingsId, behandlingsId, DateTime.now().minusDays(3), null, createXMLMeldingFraBruker("FMLI", LANG_TEKST), valueOf(oppgaveId), createXMLJourfortInformasjon(null, null, null, null))
                .withHenvendelseType(SPORSMAL_SKRIFTLIG.toString())
                .withGjeldendeTemagruppe(gjeldendeTemagruppe);
    }

    private static XMLJournalfortInformasjon createXMLJourfortInformasjon(DateTime journalfortDato, String journalfortTema, String journalfortSaksId, String journalforerNavIdent) {
        return new XMLJournalfortInformasjon()
                .withJournalfortDato(journalfortDato)
                .withJournalfortTema(journalfortTema)
                .withJournalfortSaksId(journalfortSaksId)
                .withJournalforerNavIdent(journalforerNavIdent);
    }

    private static XMLHenvendelse createXMLHenvendelse(String behandlingsId, String behandlingskjedeId, DateTime opprettet, DateTime lestDato, XMLMetadata metadata,
                                                       String oppgaveId, XMLJournalfortInformasjon journalfortInformasjon) {

        XMLHenvendelse xmlHenvendelse = new XMLHenvendelse()
                .withBehandlingsId(behandlingsId)
                .withBehandlingskjedeId(behandlingskjedeId)
                .withOpprettetDato(opprettet)
                .withLestDato(lestDato)
                .withFnr(FNR)
                .withEksternAktor(NAVIDENT)
                .withJournalfortInformasjon(journalfortInformasjon)
                .withOppgaveIdGsak(oppgaveId)
                .withErTilknyttetAnsatt(false)
                .withGjeldendeTemagruppe("ARBD");

        return xmlHenvendelse.withMetadataListe(
                metadata == null ? null : new XMLMetadataListe().withMetadata(metadata));
    }

    private static XMLHenvendelse lagDokumentVarsel(String tema, String dokumentTittel) {
        String dokumentVarselId = randomId();
        return new XMLHenvendelse()
                .withBehandlingsId(dokumentVarselId)
                .withBehandlingskjedeId(dokumentVarselId)
                .withOpprettetDato(DateTime.now().minusDays(3))
                .withTema(tema)
                .withLestDato(null)
                .withKorrelasjonsId("a1-b2")
                .withHenvendelseType(DOKUMENT_VARSEL.value())
                .withMetadataListe(new XMLMetadataListe().withMetadata(new XMLDokumentVarsel()
                        .withDokumenttittel(dokumentTittel)
                        .withJournalpostId("1")
                        .withDokumentIdListe("2")
                        .withTemanavn("Dagpenger")
                        .withFerdigstiltDato(DateTime.now()))
                );
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
        return new HenvendelsePortType() {
            @Override
            public void ping() {
            }

            @Override
            public WSHentHenvendelseResponse hentHenvendelse(WSHentHenvendelseRequest req) {
                return new WSHentHenvendelseResponse().withAny(hentHenvendelseMedBehandlingsId(req));
            }

            @Override
            public WSHentBehandlingskjedeResponse hentBehandlingskjede(WSHentBehandlingskjedeRequest wsHentBehandlingskjedeRequest) {
                return new WSHentBehandlingskjedeResponse().withAny(HENVENDELSER.get(0));
            }

            @Override
            public WSHentHenvendelseListeResponse hentHenvendelseListe(WSHentHenvendelseListeRequest wsHentHenvendelseListeRequest) {
                return new WSHentHenvendelseListeResponse().withAny(HENVENDELSER.toArray());
            }
        };
    }
}
