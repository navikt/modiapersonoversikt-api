package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.sbl.dialogarena.common.jetty.Jetty;

import java.io.IOException;
import java.net.URISyntaxException;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;

public final class StartJetty {

    public static final int PORT = 8080;

    private StartJetty() {
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        //setProperty("wicket.configuration", "development");
        //
        // setProperty("wicket.configuration", "deployment");
        System.setProperty("servicegateway.url", "https://service-gw-t8.test.local:443/");


        SystemProperties.load("/environment-test.properties");
        Jetty jetty = usingWar(WEBAPP_SOURCE).at("modiabrukerdialog").port(PORT).buildJetty();
        setupKeyAndTrustStore();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }

}
