package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.security.loginmodule.DummyRole;
import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.test.SystemProperties;
import org.eclipse.jetty.jaas.JAASLoginService;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.TEST_RESOURCES;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;


/**
 * Starter MODIA Brukerdialog lokalt p� Jetty.
 *
 * - logg p� med bruker/passord: Z000001/Gosys017,
 * - s�k etter fornavn: 'aaa'
 * - go nuts!
 */
public class StartJetty {

    public static void main(String[] args) throws IOException, NoSuchFieldException, IllegalAccessException {
        SystemProperties.setFrom("jetty-environment.properties");
        SystemProperties.setFrom("environment-t8.properties");
        TestCertificates.setupKeyAndTrustStore();

        Jetty jetty = usingWar(WEBAPP_SOURCE)
                .at("modiabrukerdialog")
                .port(8080)
                .overrideWebXml(new File(TEST_RESOURCES, "jetty-web.xml"))
                .withLoginService(createLoginService())
                .buildJetty();
	    Field contextField = jetty.getClass().getDeclaredField("context");
	    contextField.setAccessible(true);
	    WebAppContext context = (WebAppContext) contextField.get(jetty);
	    String[] resources = {"web/src/main/webapp", "../modig/modig-frontend/modig-frontend-ressurser/src/main/resources/META-INF/resources"};
	    ResourceCollection resourceCollection = new ResourceCollection(resources);
	    context.setBaseResource(resourceCollection);
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }

    private static JAASLoginService createLoginService() {
        JAASLoginService jaasLoginService = new JAASLoginService("Simple Login Realm");
        jaasLoginService.setLoginModuleName("simplelogin");
        jaasLoginService.setRoleClassNames(new String[]{DummyRole.class.getName()});
        return jaasLoginService;
    }
}
