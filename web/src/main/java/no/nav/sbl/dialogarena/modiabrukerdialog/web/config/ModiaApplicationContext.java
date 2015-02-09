package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.Hjelpetekst;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.skrivestotte.HjelpetekstIndex;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static java.util.Arrays.asList;

@Configuration
@Import({
        WicketApplicationBeans.class,
        ApplicationContextBeans.class,
        ModulesApplicationContext.class
})
public class ModiaApplicationContext {

        @Bean
        public HjelpetekstIndex hjelpetekstIndex() {
                HjelpetekstIndex hjelpetekstIndex = new HjelpetekstIndex();
                hjelpetekstIndex.indekser(asList(
                        new Hjelpetekst("Taushetsbelagt eller sensitiv informasjon",
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
                        new Hjelpetekst("Status i sak",
                                "Takk for din henvendelse til NAV. \n" +
                                        "\n" +
                                        "Din sak er fortsatt under behandling. Saksbehandlingstiden for denne type saker er normalt XX måneder. \n" +
                                        "\n" +
                                        "Du kan kontakte oss på telefon 55 55 33 33 dersom du har ytterligere spørsmål knyttet til din sak. Av hensyn til personvern og taushetsplikt kan vi ikke sende taushetsbelagt informasjon på e-post. \n",
                                "generell"),
                        new Hjelpetekst("Krav om underskrift/skannet dokument",
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
                return hjelpetekstIndex;
        }

}
