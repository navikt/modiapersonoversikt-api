package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import java.io.File;

import static no.nav.modig.core.test.FilesAndDirs.TEST_RESOURCES;
import static no.nav.modig.core.test.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.StartJetty.createLoginService;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;

public class StartJettyNoIntegration {

    public static void main(String ... args) {
        setFrom("jetty-mock-environment.properties");
        setupKeyAndTrustStore();

        usingWar(WEBAPP_SOURCE)
                .at("modiabrukerdialog")
                .withLoginService(createLoginService())
                .port(8083)
                .overrideWebXml(new File(TEST_RESOURCES, "mock-web.xml"))
                .buildJetty()
                .start();
    }

}
