package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.modiabrukerdialog.InternbrukerSubjectHandler;
import org.apache.geronimo.components.jaspi.AuthConfigFactoryImpl;

import javax.security.auth.message.config.AuthConfigFactory;
import java.io.File;
import java.security.Security;

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
        setFrom("jetty-environment.properties");
        System.setProperty("org.apache.geronimo.jaspic.configurationFile", "web/src/test/resources/jaspiconf.xml");
        Security.setProperty(AuthConfigFactory.DEFAULT_FACTORY_SECURITY_PROPERTY, AuthConfigFactoryImpl.class.getCanonicalName());
        setProperty("wicket.configuration", "development");
        InternbrukerSubjectHandler.setVeilederIdent("Z990610");
        InternbrukerSubjectHandler.setServicebruker("srvmodiabrukerdialog");
        setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", InternbrukerSubjectHandler.class.getName());
        setupKeyAndTrustStore();
    }

    private static void runJetty() {
        Jetty jetty = usingWar(WEBAPP_SOURCE)
                .at("modiabrukerdialog")
                .port(8083)
                .overrideWebXml(new File(TEST_RESOURCES, "override-web.xml"))
                .configureForJaspic()
                .buildJetty();
        jetty.start();
    }
}
