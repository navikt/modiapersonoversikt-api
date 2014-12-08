package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.config;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT_OPPMOTE;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL_SKRIFTLIG;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_SKRIFTLIG;
import static org.joda.time.DateTime.now;

@Configuration
public class JettyTestContext {

    private static Random idGenerator = new Random();

    private static final String LANG_TEKST = "Lorem ipsum dolor sit amet, " +
            "consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad " +
            "minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure " +
            "dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
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

    private static final String behandlingsId1 = randomId();
    private static final String behandlingsId2 = randomId();
    private static final String behandlingsId3 = randomId();
    private static final String behandlingsId4 = randomId();
    private static final String behandlingsId5 = randomId();
    private static final String behandlingsId6 = randomId();

    private static String randomId() {
        return String.valueOf(idGenerator.nextInt());
    }

    public static final List<XMLHenvendelse> HENVENDELSER = asList(
            createXMLHenvendelse(behandlingsId1, behandlingsId1, SPORSMAL_SKRIFTLIG, now().minusWeeks(2), null, createXMLMeldingFraBruker("ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT", LANG_TEKST)),
            createXMLHenvendelse(behandlingsId2, behandlingsId2, SPORSMAL_SKRIFTLIG, now().minusWeeks(1), null, createXMLMeldingFraBruker("INTERNASJONALT", LANG_TEKST)),
            createXMLHenvendelse(randomId(), behandlingsId2, SVAR_SKRIFTLIG, now().minusDays(5), now().minusDays(4), createXMLMeldingTilBruker("INTERNASJONALT", KORT_TEKST)),
            createXMLHenvendelse(behandlingsId3, behandlingsId3, SPORSMAL_SKRIFTLIG, now().minusDays(3), null, createXMLMeldingFraBruker("HJELPEMIDLER", LANG_TEKST)),
            createXMLHenvendelse(randomId(), behandlingsId3, SVAR_SKRIFTLIG, now().minusHours(5), null, createXMLMeldingTilBruker("HJELPEMIDLER", KORT_TEKST)),
            createXMLHenvendelse(behandlingsId4, behandlingsId4, SPORSMAL_SKRIFTLIG, now().minusMonths(4), null, createXMLMeldingFraBruker("SOSIALE_TJENESTER", LANG_TEKST)),
            createXMLHenvendelse(randomId(), behandlingsId4, SVAR_SKRIFTLIG, now().minusMonths(4).plusDays(1), now().minusMonths(4).plusDays(3), createXMLMeldingTilBruker("SOSIALE_TJENESTER", LANG_TEKST)),
            createXMLHenvendelse(randomId(), behandlingsId4, SVAR_SKRIFTLIG, now().minusDays(7), null, createXMLMeldingTilBruker("SOSIALE_TJENESTER", KORT_TEKST)),
            createXMLHenvendelse(behandlingsId5, behandlingsId5, REFERAT_OPPMOTE, now(), null, createXMLMeldingTilBruker("HJELPEMIDLER", LANG_TEKST)),
            createXMLHenvendelse(behandlingsId6, behandlingsId6, SPORSMAL_SKRIFTLIG, now().minusDays(1), null, createXMLMeldingFraBruker("ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT", LANG_TEKST))
    );

    private static XMLHenvendelse createXMLHenvendelse(String behandlingsId, String behandlingskjedeId, XMLHenvendelseType type, DateTime opprettet, DateTime lestDato, XMLMetadata metadata) {
        return new XMLHenvendelse()
                .withBehandlingsId(behandlingsId)
                .withBehandlingskjedeId(behandlingskjedeId)
                .withHenvendelseType(type.name())
                .withOpprettetDato(opprettet)
                .withLestDato(lestDato)
                .withMetadataListe(
                        new XMLMetadataListe().withMetadata(metadata));
    }

    private static XMLMeldingFraBruker createXMLMeldingFraBruker(String temagruppe, String tekst) {
        return new XMLMeldingFraBruker().withTemagruppe(temagruppe).withFritekst(tekst);
    }

    private static XMLMeldingTilBruker createXMLMeldingTilBruker(String temagruppe, String fritekst) {
        return new XMLMeldingTilBruker().withTemagruppe(temagruppe).withFritekst(fritekst);
    }


    @Bean
    public HenvendelsePortType henvendelsePortType() {
        return createHenvendelsePortTypeMock();
    }

    public static HenvendelsePortType createHenvendelsePortTypeMock() {
        return new HenvendelsePortType() {
            @Override
            public WSHentHenvendelseListeResponse hentHenvendelseListe(WSHentHenvendelseListeRequest parameters) {
                return new WSHentHenvendelseListeResponse().withAny(HENVENDELSER);
            }

            @Override
            public void ping() {
            }

            @Override
            public WSHentHenvendelseResponse hentHenvendelse(WSHentHenvendelseRequest parameters) {
                return new WSHentHenvendelseResponse();
            }
        };
    }

}

