package no.nav.sbl.dialogarena.utbetaling;

import java.io.File;

import static java.lang.System.getProperties;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.test.path.FilesAndDirs.TEST_RESOURCES;


public final class StartJettyUtbetalinger {

    public static void main(String ... args) {
        getProperties().put("server.arena.url", "arenaserver");
        usingWar(new File(TEST_RESOURCES, "webapp")).at("utbetaling").port(8383).buildJetty().start();
    }

}
