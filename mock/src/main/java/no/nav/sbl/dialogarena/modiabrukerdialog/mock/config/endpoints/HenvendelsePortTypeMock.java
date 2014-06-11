package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
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

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static org.joda.time.DateTime.now;

@Configuration
public class HenvendelsePortTypeMock {

    private static Random idGenerator = new Random();
    private static int behandlingsId = idGenerator.nextInt();

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

    public static final XMLBehandlingsinformasjon[] HENVENDELSER = {
            createXmlBehandlingsinformasjon(SPORSMAL, now().minusWeeks(2),
                    createXMLSporsmal("ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT", LANG_TEKST)),

            createXmlBehandlingsinformasjon(SPORSMAL, now().minusWeeks(1),
                    createXMLSporsmal("FAMILIE_OG_BARN", LANG_TEKST)),

            createXmlBehandlingsinformasjon(SVAR, now().minusDays(5),
                    createXMLSvar("FAMILIE_OG_BARN", String.valueOf(behandlingsId), now().minusDays(4), KORT_TEKST)),

            createXmlBehandlingsinformasjon(SPORSMAL, now().minusDays(3),
                    createXMLSporsmal("HJELPEMIDLER", LANG_TEKST)),

            createXmlBehandlingsinformasjon(SVAR, now().minusHours(5),
                    createXMLSvar("HJELPEMIDLER", String.valueOf(behandlingsId), null, KORT_TEKST)),

            createXmlBehandlingsinformasjon(SPORSMAL, now().minusMonths(4),
                    createXMLSporsmal("OVRIGE_HENVENDELSER", LANG_TEKST)),

            createXmlBehandlingsinformasjon(SVAR, now().minusMonths(4).plusDays(1),
                    createXMLSvar("OVRIGE_HENVENDELSER", String.valueOf(behandlingsId), now().minusMonths(4).plusDays(3), LANG_TEKST)),

            createXmlBehandlingsinformasjon(SVAR, now().minusDays(7),
                    createXMLSvar("OVRIGE_HENVENDELSER", String.valueOf(behandlingsId), null, KORT_TEKST)),

            createXmlBehandlingsinformasjon(REFERAT, now(),
                    createXMLReferat("HJELPEMIDLER", "TELEFON", null, LANG_TEKST)),

            createXmlBehandlingsinformasjon(SPORSMAL, now().minusDays(1),
                    createXMLSporsmal("ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT", LANG_TEKST))
    };

    private static XMLBehandlingsinformasjon createXmlBehandlingsinformasjon(XMLHenvendelseType type, DateTime opprettet, XMLMetadata metadata) {
        if (type == SPORSMAL || type == REFERAT) {
            behandlingsId = idGenerator.nextInt();
        }
        return new XMLBehandlingsinformasjon()
                .withHenvendelseType(type.name())
                .withOpprettetDato(opprettet)
                .withBehandlingsId(String.valueOf(behandlingsId))
                .withMetadataListe(
                        new XMLMetadataListe().withMetadata(metadata));
    }

    private static XMLSporsmal createXMLSporsmal(String tema, String tekst) {
        return new XMLSporsmal().withTemagruppe(tema).withFritekst(tekst);
    }

    private static XMLSvar createXMLSvar(String tema, String sporsmalsId, DateTime lestDato, String fritekst) {
        return new XMLSvar().withTemagruppe(tema).withSporsmalsId(sporsmalsId).withLestDato(lestDato).withFritekst(fritekst);
    }

    private static XMLReferat createXMLReferat(String tema, String kanal, DateTime lestDato, String tekst) {
        return new XMLReferat().withTemagruppe(tema).withKanal(kanal).withLestDato(lestDato).withFritekst(tekst);
    }

    @Bean
    public HenvendelsePortType henvendelsePortType() {
        return createHenvendelsePortTypeMock();
    }

    public static HenvendelsePortType createHenvendelsePortTypeMock() {
        return new HenvendelsePortType() {

            @Override
            public WSHentHenvendelseResponse hentHenvendelse(WSHentHenvendelseRequest wsHentHenvendelseRequest) {
                return new WSHentHenvendelseResponse();
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
