package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner.JettyNormal;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner.JettyRunner;

import static java.lang.System.setProperty;


/**
 * Starter MODIA Brukerdialog lokalt på Jetty.
 *
 * NB!
 * Sett start.properties for å styre integrasjon.
 */
public class StartJetty {

    public static void main(String[] args) {
        getJetty().run();
    }

    private static JettyRunner getJetty() {
	    setProperty("wicket.configuration", "development");
        return new JettyNormal();
    }

}
