package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.HjelpetekstIndex.HIGHLIGHTED_BEGIN;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.HjelpetekstIndex.HIGHLIGHTED_END;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class HjelpetekstIndexTest {

    private HjelpetekstIndex hjelpetekstIndex = new HjelpetekstIndex();

    @Before
    public void setup() {
        hjelpetekstIndex.indekser(lagMockHjelpetekster());
    }

    @Test
    public void skalIndeksereHjelpetekster() {
        List<Hjelpetekst> resultat = hjelpetekstIndex.sok("knadsskjemaer Taushetsbe ");

        assertThat(resultat, hasSize(1));
        assertThat(resultat.get(0).tittel, is(HIGHLIGHTED_BEGIN + "Taushetsbelagt" + HIGHLIGHTED_END + " eller sensitiv informasjon"));
    }

    @Test
    public void skalHighlighteTrefford() {
        String sokeord = "sensitiv";
        List<Hjelpetekst> resultat = hjelpetekstIndex.sok(sokeord);

        assertThat(resultat.get(0).tittel.contains(HIGHLIGHTED_BEGIN + sokeord + HIGHLIGHTED_END), is(true));
    }

    @Test
    public void kanIndeksereFlereGanger() {
        hjelpetekstIndex.indekser(lagMockHjelpetekster());
        hjelpetekstIndex.indekser(lagMockHjelpetekster());
    }

    @Test
    public void returnererAlt() {
        List<Hjelpetekst> resultat = hjelpetekstIndex.sok("");
        assertThat(resultat, hasSize(4));
    }

    @Test
    public void kanSokeKunPaaTags() {
        List<Hjelpetekst> resultat = hjelpetekstIndex.sok("", "generell");
        assertThat(resultat, hasSize(3));
    }

    @Test
    public void sokerPaaTagsMedStorForbokstav() {
        List<Hjelpetekst> resultat = hjelpetekstIndex.sok("", "generell", "feilsendt");

        assertThat(resultat, hasSize(1));
        assertThat(resultat.get(0).tittel, is("Taushetsbelagt eller sensitiv informasjon"));
    }

    @Test
    public void sokerPaaFlereTags() {
        List<Hjelpetekst> resultat = hjelpetekstIndex.sok("", "Store");

        assertThat(resultat, hasSize(1));
        assertThat(resultat.get(0).tittel, is("Test mer tags"));
    }

    @Test
    public void sokerPaaTagsErCaseInsensitive() {
        List<Hjelpetekst> resultat = hjelpetekstIndex.sok("", "stORe");

        assertThat(resultat, hasSize(1));
        assertThat(resultat.get(0).tittel, is("Test mer tags"));
    }

    @Test
    public void henterUtDetSammeSomBleIndeksert() {
        Hjelpetekst hjelpetekst = hjelpetekst("tittel", "dette er norsk", "this is english, yes!!", asList("tag1", "tag2"));
        hjelpetekstIndex.indekser(asList(hjelpetekst));
        Hjelpetekst resultat = hjelpetekstIndex.sok("").get(0);

        assertThat(resultat.tittel, is(hjelpetekst.tittel));
        assertThat(resultat.innhold, is(hjelpetekst.innhold));
        assertThat(resultat.tags, is(hjelpetekst.tags));
    }

    @Test
    public void ikkeSokbarHvisDefaultLocaleIkkeOppgitt() {
        HashMap<String, String> innhold = new HashMap<>();
        hjelpetekstIndex.indekser(asList(new Hjelpetekst("", "tittel", innhold)));

        List<Hjelpetekst> resultat = hjelpetekstIndex.sok("");
        assertThat(resultat, hasSize(0));
    }

    private static List<Hjelpetekst> lagMockHjelpetekster() {
        return asList(
                hjelpetekst(
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
                        "generell", "sensitiv", "feilsendt"),
                hjelpetekst(
                        "Status i sak",
                        "Takk for din henvendelse til NAV. \n" +
                                "\n" +
                                "Din sak er fortsatt under behandling. Saksbehandlingstiden for denne type saker er normalt XX måneder. \n" +
                                "\n" +
                                "Du kan kontakte oss på telefon 55 55 33 33 dersom du har ytterligere spørsmål knyttet til din sak. Av hensyn til personvern og taushetsplikt kan vi ikke sende taushetsbelagt informasjon på e-post. \n",
                        "generell"),
                hjelpetekst(
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
                        "generell", "sensitiv"),
                hjelpetekst("Test mer tags",
                        "Vi tester tags",
                        "tag", "Store", "bokstav"));
    }

    private static Hjelpetekst hjelpetekst(String tittel, String norsk, String... tags) {
        HashMap<String, String> innhold = new HashMap<>();
        innhold.put(Hjelpetekst.LOCALE_DEFAULT, norsk);
        return new Hjelpetekst("", tittel, innhold, tags);
    }

    private static Hjelpetekst hjelpetekst(String tittel, String norsk, String englesk, List<String> tags) {
        HashMap<String, String> innhold = new HashMap<>();
        innhold.put(Hjelpetekst.LOCALE_DEFAULT, norsk);
        innhold.put("en_US", englesk);
        return new Hjelpetekst("", tittel, innhold, tags);
    }
}