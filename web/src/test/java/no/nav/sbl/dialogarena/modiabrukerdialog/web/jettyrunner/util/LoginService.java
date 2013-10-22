package no.nav.sbl.dialogarena.modiabrukerdialog.web.jettyrunner.util;

import no.nav.modig.security.loginmodule.DummyRole;
import org.eclipse.jetty.jaas.JAASLoginService;

public class LoginService {

    public static JAASLoginService createLoginService() {
        JAASLoginService jaasLoginService = new JAASLoginService("Simple Login Realm");
        jaasLoginService.setLoginModuleName("simplelogin");
        jaasLoginService.setRoleClassNames(new String[]{DummyRole.class.getName()});
        return jaasLoginService;
    }
}
