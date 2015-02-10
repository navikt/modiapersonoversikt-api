package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.HjelpetekstIndexImpl.SPAN_CLASS_HIGHLIGHTED_BEGIN;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.HjelpetekstIndexImpl.SPAN_CLASS_HIGHLIGHTED_END;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class HjelpetekstIndexImplTest {

    private HjelpetekstIndex hjelpetekstIndex = new HjelpetekstIndexImpl();

    @Before
    public void setup() {
        hjelpetekstIndex.indekser(lagMockHjelpetekster());
    }

    @Test
    public void skalIndeksereHjelpetekster() {
        List<Hjelpetekst> resultat = hjelpetekstIndex.sok("knadsskjemaer Taushetsbe ");

        assertThat(resultat, hasSize(1));
        assertThat(resultat.get(0).tittel, is(SPAN_CLASS_HIGHLIGHTED_BEGIN + "Taushetsbelagt" + SPAN_CLASS_HIGHLIGHTED_END + " eller sensitiv informasjon"));
    }

    @Test
    public void skalHighlighteTrefford() {
        String sokeord = "sensitiv";
        List<Hjelpetekst> resultat = hjelpetekstIndex.sok(sokeord);

        assertThat(resultat.get(0).tittel.contains(SPAN_CLASS_HIGHLIGHTED_BEGIN + sokeord + SPAN_CLASS_HIGHLIGHTED_END), is(true));
    }

    @Test
    public void kanIndeksereFlereGanger() {
        hjelpetekstIndex.indekser(lagMockHjelpetekster());
        hjelpetekstIndex.indekser(lagMockHjelpetekster());
    }

    @Test
    public void returnererAlt() {
        List<Hjelpetekst> resultat = hjelpetekstIndex.sok("");
        assertThat(resultat, hasSize(3));
    }

    @Test
    public void kanSokeKunPaaTags() {
        List<Hjelpetekst> resultat = hjelpetekstIndex.sok("", "generell");
        assertThat(resultat, hasSize(3));
    }

    @Test
    public void sokerPaaFlereTags() {
        List<Hjelpetekst> resultat = hjelpetekstIndex.sok("", "generell", "feilsendt");

        assertThat(resultat, hasSize(1));
        assertThat(resultat.get(0).tittel, is("Krav om underskrift/skannet dokument"));
    }

    private static List<Hjelpetekst> lagMockHjelpetekster() {
        return asList(
                new Hjelpetekst("tausetsbelagt",
                        "Taushetsbelagt eller sensitiv informasjon",
                        "Takk for din henvendelse.\n" +
                                "\n" +
                                "Vi har dessverre ikke anledning til å besvare henvendelsen din per e-post, på grunn av personvern og taushetsplikt. \n" +
                                "\n" +
                                "Du kan kontakte oss på telefon 55 55 33 33, sende henvendelsen som ordinær post eller få informasjon ved personlig fremmøte ved ditt lokale NAV-kontor.\n" +
                                "\n" +
                                "Dersom du velger å sende inn henvendelsen per post, anbefaler vi at du henter ut en førsteside til saken din på www.nav.no. Alle dokumenter sendes til den adressen som er oppgitt på førstesiden.\n" +
                                "\n" +
                                "Søknadsskjemaer, selvbetjeningsløsninger, informasjon og «Dine utbetalinger» finner du på vår internettside www.nav.no. Her vil du også finne besøksadresse til ditt NAV-kontor.\n",
                        "generell", "sensitiv"),
                new Hjelpetekst("status",
                        "Status i sak",
                        "Takk for din henvendelse til NAV. \n" +
                                "\n" +
                                "Din sak er fortsatt under behandling. Saksbehandlingstiden for denne type saker er normalt XX måneder. \n" +
                                "\n" +
                                "Du kan kontakte oss på telefon 55 55 33 33 dersom du har ytterligere spørsmål knyttet til din sak. Av hensyn til personvern og taushetsplikt kan vi ikke sende taushetsbelagt informasjon på e-post. \n",
                        "generell"),
                new Hjelpetekst("underskrift",
                        "Krav om underskrift/skannet dokument",
                        "Takk for din henvendelse.\n" +
                                "\n" +
                                "E-posten du sendte inneholder opplysninger som må sendes inn med original underskrift. Dette av hensyn til personvern og informasjonssikkerhet.\n" +
                                "\n" +
                                "Du kan kontakte oss på telefon 55 55 33 33, sende henvendelsen som ordinær post eller få informasjon ved personlig fremmøte ved ditt lokale NAV-kontor.\n" +
                                "\n" +
                                "Dersom du velger å sende inn henvendelsen per post, anbefaler vi at du henter ut en førsteside til saken din på www.nav.no. Alle dokumenter sendes til den adressen som er oppgitt på førstesiden.\n" +
                                "\n" +
                                "Søknadsskjemaer, selvbetjeningsløsninger, informasjon og «Dine utbetalinger» finner du på vår internettside www.nav.no. Her vil du også finne besøksadresse til ditt NAV-kontor.\n",
                        "generell", "sensitiv", "feilsendt"));
    }
}