package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http;


import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.common.auth.SsoToken;
import no.nav.common.auth.Subject;
import no.nav.common.auth.SubjectHandler;
import no.nav.sbl.util.fn.UnsafeRunnable;
import no.nav.sbl.util.fn.UnsafeSupplier;

import static java.util.Collections.emptyMap;

public class SubjectHandlerUtil {

    public static <T> T withIdent(String ident, UnsafeSupplier<T> runnable) {
        Subject subject = new Subject(ident, IdentType.InternBruker, SsoToken.oidcToken("token", emptyMap()));
        return SubjectHandler.withSubject(subject, runnable);
    }

    public static void withIdent(String ident, UnsafeRunnable runnable) {
        Subject subject = new Subject(ident, IdentType.InternBruker, SsoToken.oidcToken("token", emptyMap()));
        SubjectHandler.withSubject(subject, runnable);
    }

}
