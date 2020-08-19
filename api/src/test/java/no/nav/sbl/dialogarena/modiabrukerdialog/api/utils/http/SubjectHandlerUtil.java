package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http;

import no.nav.common.auth.subject.IdentType;
import no.nav.common.auth.subject.SsoToken;
import no.nav.common.auth.subject.Subject;
import no.nav.common.auth.subject.SubjectHandler;
import no.nav.common.utils.fn.UnsafeRunnable;
import no.nav.common.utils.fn.UnsafeSupplier;

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
