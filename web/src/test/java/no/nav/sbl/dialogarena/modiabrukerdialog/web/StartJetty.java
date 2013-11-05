package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner.JettyNormal;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner.JettyRunner;

import java.io.IOException;

import static java.lang.System.setProperty;


/**
 * Starter MODIA Brukerdialog lokalt på Jetty.
 *
 * Sett start.properties for å styre integrasjon. Per default kjører alt mot reelle baksystemer
 */
public class StartJetty {

    public static void main(String[] args) throws IOException, NoSuchFieldException, IllegalAccessException {
        getJetty().run();
    }

    private static JettyRunner getJetty() {
	    setProperty("wicket.configuration", "development");
        return new JettyNormal();
    }

}
