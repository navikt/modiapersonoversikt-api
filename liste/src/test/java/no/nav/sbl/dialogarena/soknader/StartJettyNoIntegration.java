package no.nav.sbl.dialogarena.soknader;

import java.io.File;

import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.test.path.FilesAndDirs.TEST_RESOURCES;

public final class StartJettyNoIntegration {

    public static void main(String ... args) {
        usingWar(new File(TEST_RESOURCES, "webapp"))
                .at("soknader")
                .port(8383)
                .buildJetty()
                .start();
    }

}