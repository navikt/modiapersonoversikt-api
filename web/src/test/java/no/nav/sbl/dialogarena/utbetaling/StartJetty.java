package no.nav.sbl.dialogarena.utbetaling;

import java.io.File;

import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.test.path.FilesAndDirs.TEST_RESOURCES;


public final class StartJetty {
    public static void main(String ... args) {
        usingWar(new File(TEST_RESOURCES, "webapp")).at("utbetaling").port(8383).buildJetty().start();
    }
}
