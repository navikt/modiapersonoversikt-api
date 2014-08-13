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
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static org.joda.time.DateTime.now;

@Configuration
public class JettyTestContext {

    private static Random idGenerator = new Random();
    private static int behandlingsid;

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

    public static final List<XMLHenvendelse> HENVENDELSER = asList(
            createXMLHenvendelse(behandlingsid = idGenerator.nextInt(), SPORSMAL, now().minusWeeks(2), createXMLMeldingFraBruker("ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT", LANG_TEKST)),
            createXMLHenvendelse(behandlingsid = idGenerator.nextInt(), SPORSMAL, now().minusWeeks(1), createXMLMeldingFraBruker("INTERNASJONALT", LANG_TEKST)),
            createXMLHenvendelse(behandlingsid, SVAR, now().minusDays(5), createXMLMeldingTilBruker("INTERNASJONALT", String.valueOf(behandlingsid), now().minusDays(4), KORT_TEKST)),
            createXMLHenvendelse(behandlingsid = idGenerator.nextInt(), SPORSMAL, now().minusDays(3), createXMLMeldingFraBruker("HJELPEMIDLER", LANG_TEKST)),
            createXMLHenvendelse(behandlingsid, SVAR, now().minusHours(5), createXMLMeldingTilBruker("HJELPEMIDLER", String.valueOf(behandlingsid), null, KORT_TEKST)),
            createXMLHenvendelse(behandlingsid = idGenerator.nextInt(), SPORSMAL, now().minusMonths(4), createXMLMeldingFraBruker("SOSIALE_TJENESTER", LANG_TEKST)),
            createXMLHenvendelse(behandlingsid, SVAR, now().minusMonths(4).plusDays(1), createXMLMeldingTilBruker("SOSIALE_TJENESTER", String.valueOf(behandlingsid), now().minusMonths(4).plusDays(3), LANG_TEKST)),
            createXMLHenvendelse(behandlingsid, SVAR, now().minusDays(7), createXMLMeldingTilBruker("SOSIALE_TJENESTER", String.valueOf(behandlingsid), null, KORT_TEKST)),
            createXMLHenvendelse(behandlingsid = idGenerator.nextInt(), REFERAT, now(), createXMLMeldingTilBruker("HJELPEMIDLER", "TELEFON", null, LANG_TEKST)),
            createXMLHenvendelse(behandlingsid = idGenerator.nextInt(), SPORSMAL, now().minusDays(1), createXMLMeldingFraBruker("ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT", LANG_TEKST))
    );

    private static XMLHenvendelse createXMLHenvendelse(int behandlingsId, XMLHenvendelseType type, DateTime opprettet, XMLMetadata metadata) {
        return new XMLHenvendelse()
                .withHenvendelseType(type.name())
                .withOpprettetDato(opprettet)
                .withBehandlingsId(String.valueOf(behandlingsId))
                .withMetadataListe(
                        new XMLMetadataListe().withMetadata(metadata));
    }

    private static XMLMeldingFraBruker createXMLMeldingFraBruker(String temagruppe, String tekst) {
        return new XMLMeldingFraBruker().withTemagruppe(temagruppe).withFritekst(tekst);
    }

    private static XMLMeldingTilBruker createXMLMeldingTilBruker(String temagruppe, String sporsmalsId, DateTime lestDato, String fritekst) {
        return new XMLMeldingTilBruker().withTemagruppe(temagruppe).withSporsmalsId(sporsmalsId).withLestDato(lestDato).withFritekst(fritekst);
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

