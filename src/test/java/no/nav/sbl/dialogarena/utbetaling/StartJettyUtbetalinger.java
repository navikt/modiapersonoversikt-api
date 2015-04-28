package no.nav.sbl.dialogarena.utbetaling;

import java.io.File;

import static java.lang.System.setProperty;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.test.path.FilesAndDirs.TEST_RESOURCES;


public final class StartJettyUtbetalinger {

    private static final String REAL_DATA = "false";
    private static final String MOCK_DATA = "true";

    public static void main(String ... args) {
        setupProperties();
        setupKeyAndTrustStore();

        usingWar(new File(TEST_RESOURCES, "webapp"))
                .at("utbetaling")
                .port(8383)
                .buildJetty()
                .start();
    }

    private static void setupProperties() {
        setProperty("wicket.configuration", "development");
        setProperty("utbetal.endpoint.mock", REAL_DATA);
        setProperty("server.arena.url", "arenaserver");
        setProperty("utbetalingendpoint.url", "https://wasapp-t4.adeo.no/nav-utbetaldata-ws/virksomhet/Utbetaling_v1");
    }

}
