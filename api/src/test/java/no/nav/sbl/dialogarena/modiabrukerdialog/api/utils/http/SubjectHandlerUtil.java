package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http;

import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.brukerdialog.tools.SecurityConstants;
import no.nav.common.auth.SsoToken;
import no.nav.common.auth.Subject;
import no.nav.common.auth.SubjectHandler;
import no.nav.sbl.util.fn.UnsafeRunnable;

import static java.util.Collections.emptyMap;

public class SubjectHandlerUtil {

    public static void medSaksbehandler(String ident, UnsafeRunnable runnable) {
        System.setProperty(SecurityConstants.SYSTEMUSER_USERNAME, "srvModiabrukerdialog");
        SubjectHandler.withSubject(
                new Subject(ident, IdentType.InternBruker, SsoToken.oidcToken("", emptyMap())),
                runnable
        );
    }

}
