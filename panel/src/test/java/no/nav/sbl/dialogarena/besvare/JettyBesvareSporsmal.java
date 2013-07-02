package no.nav.sbl.dialogarena.besvare;

import no.nav.sbl.dialogarena.common.jetty.Jetty;

import java.io.File;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.sbl.dialogarena.test.path.FilesAndDirs.TEST_RESOURCES;


public final class JettyBesvareSporsmal {

    public static void main(String ... args) {
        Jetty jetty = Jetty.usingWar(new File(TEST_RESOURCES, "webapp")).buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }
}
