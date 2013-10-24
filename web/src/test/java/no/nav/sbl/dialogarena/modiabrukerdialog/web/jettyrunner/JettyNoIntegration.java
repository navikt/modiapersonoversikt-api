package no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner;

import no.nav.sbl.dialogarena.common.jetty.Jetty;

import java.io.File;

import static no.nav.modig.core.test.FilesAndDirs.TEST_RESOURCES;
import static no.nav.modig.core.test.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner.util.LoginService.createLoginService;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;

public class JettyNoIntegration implements JettyRunner {

    @Override
    public void run() {
        setup().start();
    }

    private Jetty setup() {
        setFrom("jetty-mock-environment.properties");
        setupKeyAndTrustStore();

        return usingWar(WEBAPP_SOURCE)
                .at("modiabrukerdialog")
                .withLoginService(createLoginService())
                .port(8083)
                .overrideWebXml(new File(TEST_RESOURCES, "mock-web.xml"))
                .buildJetty();
    }
}
