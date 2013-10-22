package no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner;

import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner.util.LoginService;

import java.io.File;

import static no.nav.modig.core.test.FilesAndDirs.TEST_RESOURCES;
import static no.nav.modig.core.test.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;


public class JettyNoIntegration implements JettyRunner {
    private Jetty jetty;

    @Override
    public void run() {
        System.out.println("Run JettyNoIntegration");
        setup();
        jetty.start();
    }

    private JettyRunner setup() {
        setFrom("jetty-mock-environment.properties");
        setupKeyAndTrustStore();

        jetty = usingWar(WEBAPP_SOURCE)
                .at("modiabrukerdialog")
                .withLoginService(LoginService.createLoginService())
                .port(8083)
                .overrideWebXml(new File(TEST_RESOURCES, "mock-web.xml"))
                .buildJetty();

        return this;
    }
}
