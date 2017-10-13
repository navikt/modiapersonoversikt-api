package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.security.loginmodule.DummyRole;
import org.eclipse.jetty.jaas.JAASLoginService;

import java.io.File;

import static java.lang.System.setProperty;
import static no.nav.modig.core.test.FilesAndDirs.TEST_RESOURCES;
import static no.nav.modig.core.test.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;

public class StartJetty {

    public static void main(String[] args) {
        setupProperties();
        runJetty();
    }

    private static void setupProperties() {
        setProperty("wicket.configuration", "development");
        setFrom("jetty-environment.properties");
        setupKeyAndTrustStore();
    }

    private static void runJetty() {
        usingWar(WEBAPP_SOURCE)
                .at("modiabrukerdialog")
                .port(8083)
                .overrideWebXml(new File(TEST_RESOURCES, "override-web.xml"))
                .withLoginService(createLoginService())
                .buildJetty()
                .start();
    }

    public static JAASLoginService createLoginService() {
        JAASLoginService jaasLoginService = new JAASLoginService("Simple Login Realm");
        jaasLoginService.setLoginModuleName("simplelogin");
        jaasLoginService.setRoleClassNames(new String[]{DummyRole.class.getName()});
        return jaasLoginService;
    }

}
