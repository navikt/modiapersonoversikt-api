package no.nav.sbl.dialogarena;

import no.nav.sbl.jetty.Jetty;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.sbl.jetty.Jetty.usingWar;

public final class StartJetty {

    public static final int PORT = 8080;

    public static void main(String[] args) {
        Jetty jetty = usingWar(WEBAPP_SOURCE).at("modiabrukerdialog").port(PORT).buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }

    private StartJetty() { }

}
