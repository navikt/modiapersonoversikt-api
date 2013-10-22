package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner.JettyNoIntegration;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner.JettyNormal;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner.JettyRunner;

import java.io.IOException;
import java.util.Properties;

import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;


/**
 * Starter MODIA Brukerdialog lokalt på Jetty.
 *
 * Bruk start.properties for å styre hvilken JettyRunner som starter.
 * Sett start.jetty.withintegration=no hvis du ønsker JettyNoIntegration, ellers
 * starter JettyNormal.
 *
 * - logg p? med bruker/passord: Z000001/Gosys017,
 * - s?k etter fornavn: 'aaa'
 * - go nuts!
 */
public class StartJetty {

    public static void main(String[] args) throws IOException, NoSuchFieldException, IllegalAccessException {
        JettyRunner jetty = getJetty();
        jetty.run();
    }

    private static JettyRunner getJetty() {
        setFrom("start.properties");

        Properties properties = System.getProperties();
        String start = properties.getProperty("start.jetty.withintegration");

        if (start.compareToIgnoreCase("no") == 0) {
            return new JettyNoIntegration();
        }
        return new JettyNormal();
    }

}
