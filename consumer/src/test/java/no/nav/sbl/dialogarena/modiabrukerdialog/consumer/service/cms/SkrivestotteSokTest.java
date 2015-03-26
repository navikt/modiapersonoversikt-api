package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.SkrivestotteSok.HIGHLIGHTED_BEGIN;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.SkrivestotteSok.HIGHLIGHTED_END;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class SkrivestotteSokTest {

    private SkrivestotteSok skrivestotteSok = new SkrivestotteSok();

    @Before
    public void setup() {
        skrivestotteSok.indekser(lagMockSkrivestotteTekster());
    }

    @Test
    public void skalIndeksereSkrivestotteTekster() {
        List<SkrivestotteTekst> resultat = skrivestotteSok.sok("knadsskjemaer Taushetsbe ");

        assertThat(resultat, hasSize(1));
        assertThat(resultat.get(0).tittel, is(HIGHLIGHTED_BEGIN + "Taushetsbelagt" + HIGHLIGHTED_END + " eller sensitiv informasjon"));
    }

    @Test
    public void skalHighlighteTrefford() {
        String sokeord = "sensitiv";
        List<SkrivestotteTekst> resultat = skrivestotteSok.sok(sokeord);

        assertThat(resultat.get(0).tittel.contains(HIGHLIGHTED_BEGIN + sokeord + HIGHLIGHTED_END), is(true));
    }

    @Test
    public void kanIndeksereFlereGanger() {
        skrivestotteSok.indekser(lagMockSkrivestotteTekster());
        skrivestotteSok.indekser(lagMockSkrivestotteTekster());
    }

    @Test
    public void returnererAlt() {
        List<SkrivestotteTekst> resultat = skrivestotteSok.sok("");
        assertThat(resultat, hasSize(4));
    }

    @Test
    public void kanSokeKunPaaTags() {
        List<SkrivestotteTekst> resultat = skrivestotteSok.sok("", "generell");
        assertThat(resultat, hasSize(3));
    }

    @Test
    public void sokerPaaTagsMedStorForbokstav() {
        List<SkrivestotteTekst> resultat = skrivestotteSok.sok("", "generell", "feilsendt");

        assertThat(resultat, hasSize(1));
        assertThat(resultat.get(0).tittel, is("Taushetsbelagt eller sensitiv informasjon"));
    }

    @Test
    public void sokerPaaFlereTags() {
        List<SkrivestotteTekst> resultat = skrivestotteSok.sok("", "Store");

        assertThat(resultat, hasSize(1));
        assertThat(resultat.get(0).tittel, is("Test mer tags"));
    }

    @Test
    public void sokerPaaTagsErCaseInsensitive() {
        List<SkrivestotteTekst> resultat = skrivestotteSok.sok("", "stORe");

        assertThat(resultat, hasSize(1));
        assertThat(resultat.get(0).tittel, is("Test mer tags"));
    }

    @Test
    public void henterUtDetSammeSomBleIndeksert() {
        SkrivestotteTekst skrivestotteTekst = skrivestotteTekst("tittel", "dette er norsk", "this is english, yes!!", asList("tag1", "tag2"));
        skrivestotteSok.indekser(asList(skrivestotteTekst));
        SkrivestotteTekst resultat = skrivestotteSok.sok("").get(0);

        assertThat(resultat.tittel, is(skrivestotteTekst.tittel));
        assertThat(resultat.innhold, is(skrivestotteTekst.innhold));
        assertThat(resultat.tags, is(skrivestotteTekst.tags));
    }

    @Test
    public void ikkeSokbarHvisDefaultLocaleIkkeOppgitt() {
        HashMap<String, String> innhold = new HashMap<>();
        skrivestotteSok.indekser(asList(new SkrivestotteTekst("", "tittel", innhold)));

        List<SkrivestotteTekst> resultat = skrivestotteSok.sok("");
        assertThat(resultat, hasSize(0));
    }

    private static List<SkrivestotteTekst> lagMockSkrivestotteTekster() {
        return asList(
                skriveStotteTekst(
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
                skriveStotteTekst(
                        "Status i sak",
                        "Takk for din henvendelse til NAV. \n" +
                                "\n" +
                                "Din sak er fortsatt under behandling. Saksbehandlingstiden for denne type saker er normalt XX måneder. \n" +
                                "\n" +
                                "Du kan kontakte oss på telefon 55 55 33 33 dersom du har ytterligere spørsmål knyttet til din sak. Av hensyn til personvern og taushetsplikt kan vi ikke sende taushetsbelagt informasjon på e-post. \n",
                        "generell"),
                skriveStotteTekst(
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
                skriveStotteTekst("Test mer tags",
                        "Vi tester tags",
                        "tag", "Store", "bokstav"));
    }

    private static SkrivestotteTekst skriveStotteTekst(String tittel, String norsk, String... tags) {
        HashMap<String, String> innhold = new HashMap<>();
        innhold.put(SkrivestotteTekst.LOCALE_DEFAULT, norsk);
        return new SkrivestotteTekst("", tittel, innhold, tags);
    }

    private static SkrivestotteTekst skrivestotteTekst(String tittel, String norsk, String englesk, List<String> tags) {
        HashMap<String, String> innhold = new HashMap<>();
        innhold.put(SkrivestotteTekst.LOCALE_DEFAULT, norsk);
        innhold.put("en_US", englesk);
        return new SkrivestotteTekst("", tittel, innhold, tags);
    }
}