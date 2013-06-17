package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.common.jetty.Jetty;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;


public final class StartJetty {


    public static final int PORT = 8080;

    private StartJetty() {
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        SystemProperties.load("/environment-test.properties");
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
//        System.setProperty("spring.profiles.active", "test");
        TestCertificates.setupKeyAndTrustStore();

        Jetty jetty = usingWar(WEBAPP_SOURCE).at("modiabrukerdialog")
                .overrideWebXml(new File("src/test/resources/jetty-web.xml")).port(PORT).buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }

}
