package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner.JettyNoIntegration;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner.JettyNormal;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner.JettyRunner;

import java.io.IOException;

import static java.lang.System.getProperties;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;


/**
 * Starter MODIA Brukerdialog lokalt på Jetty.
 *
 * Bruk start.properties for å styre hvilken JettyRunner som starter.
 * Sett start.jetty.withintegration=no hvis du ønsker JettyNoIntegration, ellers
 * starter JettyNormal.
 *
 * MED INTEGRASJON
 * - logg på med bruker/passord: Z000001/Gosys017,
 * - søk etter fornavn: 'aaa'
 * - go nuts!
 *
 * UTEN INTEGRASJON
 * - innlogging med valgfri bruker/passord (har ingenting å si)
 * - søk på et gyldig fødselsnummer (man får en statisk person tilbake uansett)
 * - si fra dersom noe oppfører seg mangelfullt
 */
public class StartJetty {

    public static void main(String[] args) throws IOException, NoSuchFieldException, IllegalAccessException {
        getJetty().run();
    }

    private static JettyRunner getJetty() {
        setFrom("start.properties");

        String start = getProperties().getProperty("start.jetty.withintegration");

        if (start.compareToIgnoreCase("no") == 0) {
            return new JettyNoIntegration();
        }
        return new JettyNormal();
    }

}
