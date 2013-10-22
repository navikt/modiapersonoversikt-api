package no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner;

import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner.util.LoginService;

import java.io.File;

import static no.nav.modig.core.test.FilesAndDirs.TEST_RESOURCES;
import static no.nav.modig.core.test.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;


public class JettyNormal implements JettyRunner {

    private Jetty jetty;

    @Override
    public JettyRunner setup() {
        setFrom("jetty-environment.properties");
        setFrom("environment-local.properties");
        setupKeyAndTrustStore();

        jetty = usingWar(WEBAPP_SOURCE)
                .at("modiabrukerdialog")
                .port(8083)
                .overrideWebXml(new File(TEST_RESOURCES, "override-web.xml"))
                .withLoginService(LoginService.createLoginService())
                .buildJetty();

        return this;
    }

    @Override
    public void run() {
        System.out.println("Run JettyNormal");
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }

}
