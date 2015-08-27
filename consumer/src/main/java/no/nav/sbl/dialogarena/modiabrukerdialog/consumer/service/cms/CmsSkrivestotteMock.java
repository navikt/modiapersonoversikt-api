package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;

public class CmsSkrivestotteMock implements CmsSkrivestotte {

    private static int key = 0;
    private static final int RANDOM_TEKSTER = 20;

    @Override
    public List<SkrivestotteTekst> hentSkrivestotteTekster() {
        List<SkrivestotteTekst> tekster = new ArrayList<>();

        tekster.addAll(asList(
                skrivestotteTekst("Lang tekst",
                        "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?\n" +
                        "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?\n" +
                        "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?\n" +
                        "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?\n" +
                        "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?\n" +
                        "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?\n" +
                        "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?\n" +
                        "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?\n" +
                        "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi commodi corporis, ipsum nemo nesciunt non provident quis rem soluta temporibus ut vel vitae voluptas voluptatem voluptatibus. Ipsam minima nobis voluptatum?",
                        "lang", "scroll"),
                skrivestotteTekst(
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
                skrivestotteTekst(
                        "Status i sak",
                        "Takk for din henvendelse til NAV. \n" +
                                "\n" +
                                "Din sak er fortsatt under behandling. Saksbehandlingstiden for denne type saker er normalt XX måneder. \n" +
                                "\n" +
                                "Du kan kontakte oss på telefon 55 55 33 33 dersom du har ytterligere spørsmål knyttet til din sak. Av hensyn til personvern og taushetsplikt kan vi ikke sende taushetsbelagt informasjon på e-post. \n",
                        "generell"),
                skrivestotteTekst(
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
                        "generell", "sensitiv", "feilsendt")));

        for (int i = 0; i < RANDOM_TEKSTER; i++) {
            tekster.add(skrivestotteTekst("Random tekst " + i, "Norsk tekst streng " + i, "random", "id"+i));
        }

        return tekster;
    }

    private static SkrivestotteTekst skrivestotteTekst(String tittel, String norsk, String... tags) {
        HashMap<String, String> innhold = new HashMap<>();
        innhold.put(SkrivestotteTekst.LOCALE_DEFAULT, norsk);
        return new SkrivestotteTekst(String.valueOf(key++), tittel, innhold, tags);
    }
}
