package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.security.loginmodule.DummyRole;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import org.eclipse.jetty.jaas.JAASLoginService;

import static java.lang.Boolean.TRUE;
import static java.lang.System.setProperty;
import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TLSOppsettUtils.SKRU_AV_SERTIFIKATSJEKK_LOKALT;


/**
 * Starter MODIA Brukerdialog lokalt på Jetty.
 * <p/>
 * NB!
 * Sett start.properties for å styre integrasjon.
 */
public final class StartJetty {

    public static void main(String... args) throws Exception {
        setProperty("wicket.configuration", "development");
        setProperty(SKRU_AV_SERTIFIKATSJEKK_LOKALT, TRUE.toString());

        Jetty jetty = usingWar()
                .at("modiabrukerdialog")
                .port(8083)
                .loadProperties("/jetty-environment.properties")
                .overrideWebXml()
                .withLoginService(createLoginService())
                .buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }

    public static JAASLoginService createLoginService() {
        JAASLoginService jaasLoginService = new JAASLoginService("Simple Login Realm");
        jaasLoginService.setLoginModuleName("simplelogin");
        jaasLoginService.setRoleClassNames(new String[]{DummyRole.class.getName()});
        return jaasLoginService;
    }

}
