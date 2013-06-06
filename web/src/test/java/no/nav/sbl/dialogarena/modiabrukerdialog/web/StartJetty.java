package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;

import java.io.IOException;
import java.net.URISyntaxException;

import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.common.jetty.Jetty;

public final class StartJetty {

    public static final int PORT = 8080;

    private StartJetty() {
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        SystemProperties.load("/environment-test.properties");
        TestCertificates.setupKeyAndTrustStore();

        Jetty jetty = usingWar(WEBAPP_SOURCE).at("modiabrukerdialog").port(PORT).buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }

}
