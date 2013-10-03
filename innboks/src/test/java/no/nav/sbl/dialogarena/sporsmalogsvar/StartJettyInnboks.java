package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.test.SystemProperties;

import java.io.File;

import static no.nav.sbl.dialogarena.test.path.FilesAndDirs.TEST_RESOURCES;

public final class StartJettyInnboks {

    public static void main(String ... args) {
        SystemProperties.setFrom("jetty-innside.properties");
        Jetty.usingWar(new File(TEST_RESOURCES, "webapp")).at("innboks").port(8383)
                .buildJetty().start();
    }
}
