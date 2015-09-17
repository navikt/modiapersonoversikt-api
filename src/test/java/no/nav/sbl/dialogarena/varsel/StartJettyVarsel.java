package no.nav.sbl.dialogarena.varsel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.setProperty;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.test.path.FilesAndDirs.TEST_RESOURCES;


public final class StartJettyVarsel {

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
        setProperty("utbetalingendpoint.url", getEndpointUrl("T4"));
    }

    private static String getEndpointUrl(String env) {
        Map<String, String> urls = new HashMap<>();
        urls.put("T4", "https://wasapp-t4.adeo.no/nav-utbetaldata-ws/virksomhet/Utbetaling_v1");
        urls.put("U1", "https://e34wasl00319.devillo.no:9443/nav-utbetaldata-ws/virksomhet/Utbetaling_v1");

        return urls.get(env);
    }

}
