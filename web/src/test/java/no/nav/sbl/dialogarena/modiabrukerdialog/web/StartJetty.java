package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.security.loginmodule.DummyRole;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.test.SystemProperties;
import org.eclipse.jetty.jaas.JAASLoginService;

import java.io.File;
import java.io.IOException;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.TEST_RESOURCES;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;

public class StartJetty {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        SystemProperties.setFrom("jetty-environment.properties");
        SystemProperties.setFrom("environment-t8.properties");
        setupKeyAndTrustStore();
        Jetty jetty = usingWar(WEBAPP_SOURCE)
                .at("modiabrukerdialog")
                .port(PORT)
                .overrideWebXml(new File(TEST_RESOURCES, "jetty-web.xml"))
                .withLoginService(createLoginService())
                .buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }

    private static JAASLoginService createLoginService() {
        JAASLoginService jaasLoginService = new JAASLoginService("Simple Login Realm");
        jaasLoginService.setLoginModuleName("simplelogin");
        jaasLoginService.setRoleClassNames(new String[]{DummyRole.class.getName()});
        return jaasLoginService;
    }
}
