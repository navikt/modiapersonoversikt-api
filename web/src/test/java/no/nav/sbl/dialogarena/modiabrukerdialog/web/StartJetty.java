package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.brukerdialog.security.context.InternbrukerSubjectHandler;
import no.nav.brukerdialog.security.domain.OidcCredential;
import no.nav.dialogarena.config.fasit.FasitUtils;
import no.nav.dialogarena.config.fasit.TestUser;
import no.nav.dialogarena.config.security.ISSOProvider;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.test.SystemProperties;
import org.apache.geronimo.components.jaspi.AuthConfigFactoryImpl;

import javax.security.auth.message.config.AuthConfigFactory;
import java.io.File;

import static java.lang.System.setProperty;
import static no.nav.modig.core.test.FilesAndDirs.TEST_RESOURCES;
import static no.nav.modig.core.test.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;

public class StartJetty {

    public static final String SERVICEBRUKER = "srvmodiabrukerdialog";
    public static final String INNLOGGET_VEILEDER = FasitUtils.getVariable("veilederident");
    public static final String INNLOGGET_VEILEDER_PASSORD = FasitUtils.getVariable("veilederpassord");

    public static void main(String[] args) {
        setupProperties();
        runJetty();
    }

    private static void setupProperties() {
        SystemProperties.setFrom("jetty-environment.properties");
        setProperty("org.apache.geronimo.jaspic.configurationFile", "web/src/test/resources/jaspiconf.xml");
        setProperty(AuthConfigFactory.DEFAULT_FACTORY_SECURITY_PROPERTY, AuthConfigFactoryImpl.class.getCanonicalName());
        setProperty("wicket.configuration", "development");

        sjekkAtNodvendigeSystemPropertiesErSatt();

        setupKeyAndTrustStore();

        setUpOidcSubjectHandler();
    }

    private static void sjekkAtNodvendigeSystemPropertiesErSatt() {
        FasitUtils.getVariable("domenebrukernavn");
        FasitUtils.getVariable("domenepassord");
    }

    private static OidcCredential setUpOidcSubjectHandler() {
        TestUser testUser = new TestUser().setUsername(INNLOGGET_VEILEDER).setPassword(INNLOGGET_VEILEDER_PASSORD);
        OidcCredential credential = new OidcCredential(ISSOProvider.getISSOToken(testUser));
        InternbrukerSubjectHandler.setVeilederIdent(INNLOGGET_VEILEDER);
        InternbrukerSubjectHandler.setServicebruker(SERVICEBRUKER);
        InternbrukerSubjectHandler.setOidcCredential(credential);
        setProperty("no.nav.brukerdialog.security.context.subjectHandlerImplementationClass", InternbrukerSubjectHandler.class.getName());
        return credential;
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