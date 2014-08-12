package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSvar;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseResponse;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

import static java.lang.String.valueOf;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static org.joda.time.DateTime.now;

@Configuration
public class HenvendelsePortTypeMock {

    private static Random idGenerator = new Random();
    private static int behandlingsId = idGenerator.nextInt();
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

    public static final XMLHenvendelse[] HENVENDELSER = {
            createXMLHenvendelse(SPORSMAL, now().minusWeeks(2),
                    createXMLSporsmal("ARBD", LANG_TEKST, valueOf(oppgaveId++)), now().minusDays(2), "Arbeidsavklaring", "", ""),

            createXMLHenvendelse(SPORSMAL, now().minusWeeks(1),
                    createXMLSporsmal("FMLI", LANG_TEKST, valueOf(oppgaveId)), now().minusDays(2), "Foreldrepenger", "", ""),

            createXMLHenvendelse(SVAR, now().minusDays(2),
                    createXMLSvar("FMLI", "TELEFON", valueOf(oppgaveId), now().minusDays(4), "Vi kan bekrefte at du får foreldrepenger"), null, "", "", ""),

            createXMLHenvendelse(SVAR, now().minusDays(3),
                    createXMLSvar("FMLI", "TELEFON", valueOf(oppgaveId), now().minusDays(4), "Det er meget sannsynlig at du kan få foreldrepenger"),
                    now().minusDays(2), "Foreldrepenger", JOURNALFORT_SAKSID_FORELDREPENGER, JOURNALFORER_NAV_IDENT),

            createXMLHenvendelse(SVAR, now().minusDays(5),
                    createXMLSvar("FMLI", "TELEFON", valueOf(oppgaveId), now().minusDays(5), "Det kan hende at du kan få foredrepenger "),
                    now().minusDays(3), "Foreldrepenger", JOURNALFORT_SAKSID_FORELDREPENGER, JOURNALFORER_NAV_IDENT),

            createXMLHenvendelse(SVAR, now().minusDays(5),
                    createXMLSvar("FMLI", "TELEFON", valueOf(oppgaveId), now().minusDays(6), "Vi har hatt en samtale og det kommer frem at Test Testesen ønsker foreldrepenger "),
                    now().minusDays(3), "Foreldrepenger", JOURNALFORT_SAKSID_FORELDREPENGER, JOURNALFORER_NAV_IDENT),

            createXMLHenvendelse(SPORSMAL, now().minusDays(3),
                    createXMLSporsmal("ARBD", LANG_TEKST, valueOf(oppgaveId++)),
                    now().minusDays(1), "Hjelpemidler", JOURNALFORT_SAKSID_HJELPEMIDLER, JOURNALFORER_NAV_IDENT),

            createXMLHenvendelse(SVAR, now().minusHours(5),
                    createXMLSvar("ARBD", "TEKST", valueOf(behandlingsId), null, KORT_TEKST),
                    now().minusDays(1), "Hjelpemidler", JOURNALFORT_SAKSID_HJELPEMIDLER, JOURNALFORER_NAV_IDENT),

            createXMLHenvendelse(SPORSMAL, now().minusMonths(4),
                    createXMLSporsmal("ARBD", LANG_TEKST, valueOf(oppgaveId++)), null, "", "", ""),

            createXMLHenvendelse(SVAR, now().minusMonths(4).plusDays(1),
                    createXMLSvar("ARBD", "TELEFON", valueOf(behandlingsId), now().minusMonths(4).plusDays(3), LANG_TEKST), now().minusDays(3), "Ovrige", "", ""),

            createXMLHenvendelse(SVAR, now().minusDays(7),
                    createXMLSvar("ARBD", "TEKST", valueOf(behandlingsId), null, KORT_TEKST), now().minusDays(3), "Ovrige", "", ""),

            createXMLHenvendelse(REFERAT, now(),
                    createXMLReferat("ARBD", "TELEFON", null, "Test Testesen er utålmodig på å få utbetalt dagpengene sine"), null, "", "", ""),

            createXMLHenvendelse(SPORSMAL, now().minusDays(1),
                    createXMLSporsmal("ARBD", LANG_TEKST, valueOf(oppgaveId++)), null, "", "", "")
    };

    private static XMLHenvendelse createXMLHenvendelse(XMLHenvendelseType type, DateTime opprettet, XMLMetadata metadata,
                                                       DateTime journalfortDato, String journalfortTema, String journalfortSaksId, String journalforerNavIdent) {
        behandlingsId = idGenerator.nextInt();
        return new XMLHenvendelse()
                .withHenvendelseType(type.name())
                .withOpprettetDato(opprettet)
                .withBehandlingsId(valueOf(behandlingsId))
                .withFnr(FNR)
                .withJournalfortInformasjon(
                        new XMLJournalfortInformasjon()
                                .withJournalfortDato(journalfortDato)
                                .withJournalfortTema(journalfortTema)
                                .withJournalfortSaksId(journalfortSaksId)
                                .withJournalforerNavIdent(journalforerNavIdent)
                )
                .withMetadataListe(
                        new XMLMetadataListe().withMetadata(metadata));
    }

    private static XMLSporsmal createXMLSporsmal(String temagruppe, String tekst, String oppgaveId) {
        return new XMLSporsmal().withTemagruppe(temagruppe).withFritekst(tekst).withOppgaveIdGsak(oppgaveId);
    }

    private static XMLSvar createXMLSvar(String temagruppe, String kanal, String sporsmalsId, DateTime lestDato, String fritekst) {
        return new XMLSvar().withTemagruppe(temagruppe).withKanal(kanal).withSporsmalsId(sporsmalsId).withLestDato(lestDato).withFritekst(fritekst).withNavident(NAVIDENT);
    }

    private static XMLReferat createXMLReferat(String temagruppe, String kanal, DateTime lestDato, String tekst) {
        return new XMLReferat().withTemagruppe(temagruppe).withKanal(kanal).withLestDato(lestDato).withFritekst(tekst).withNavident(NAVIDENT);
    }

    @Bean
    public HenvendelsePortType henvendelsePortType() {
        return createHenvendelsePortTypeMock();
    }

    public static HenvendelsePortType createHenvendelsePortTypeMock() {
        return new HenvendelsePortType() {

            @Override
            public WSHentHenvendelseResponse hentHenvendelse(WSHentHenvendelseRequest wsHentHenvendelseRequest) {
                XMLHenvendelse henvendelse = new XMLHenvendelse();
                for (XMLHenvendelse xmlBehandlingsinformasjon : HENVENDELSER) {
                    if (xmlBehandlingsinformasjon.getBehandlingsId().equals(wsHentHenvendelseRequest.getBehandlingsId())) {
                        henvendelse = xmlBehandlingsinformasjon;
                    }
                }
                return new WSHentHenvendelseResponse().withAny(henvendelse);
            }

            @Override
            public WSHentHenvendelseListeResponse hentHenvendelseListe(WSHentHenvendelseListeRequest wsHentHenvendelseListeRequest) {
                return new WSHentHenvendelseListeResponse().withAny(HENVENDELSER);
            }

            @Override
            public void ping() {
            }

        };
    }
}
