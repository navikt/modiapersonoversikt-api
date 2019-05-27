package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import less.LessResourceMarker;
import no.nav.brukerdialog.security.context.InternbrukerSubjectHandler;
import no.nav.brukerdialog.security.domain.OidcCredential;
import no.nav.dialogarena.config.fasit.FasitUtils;
import no.nav.dialogarena.config.fasit.TestUser;
import no.nav.dialogarena.config.security.ISSOProvider;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.test.SystemProperties;
import org.apache.geronimo.components.jaspi.AuthConfigFactoryImpl;
import org.eclipse.jetty.util.resource.JarResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;

import javax.security.auth.message.config.AuthConfigFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

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
        SystemProperties.setFrom("jetty-environment-q0.properties");
        try {
            SystemProperties.setFrom(new FileInputStream("credentials.properties"));
        } catch (FileNotFoundException e) {
            System.err.println("credentials.properties mangler. Kopier den fra noen andre!");
        }
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
                .buildJetty();

        // Servlet 3.0-specen fungerer ikke optimalt når man starter fra ide/jar.
        // Bytter ut WebInfConfig med en subclass av den som manuelt legger til modig-frontend
        byttUtWebInfConfiguration(jetty.context);

        jetty.start();
    }

    private static void byttUtWebInfConfiguration(WebAppContext context) {
        String[] configurations = Arrays.stream(context.getConfigurationClasses())
                .map(className -> {
                    if (WebInfConfiguration.class.getName().equals(className)) {
                        return CustomWebInfConfiguration.class.getName();
                    } else {
                        return className;
                    }
                })
                .toArray(String[]::new);
        context.setConfigurationClasses(configurations);
    }

    public static class CustomWebInfConfiguration extends WebInfConfiguration {
        @Override
        protected List<Resource> findJars(WebAppContext context) throws Exception {
            List<Resource> jars = super.findJars(context);
            jars.add(JarResource.newResource(LessResourceMarker.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
            return jars;
        }
    }
}