package no.nav.modiapersonoversikt.legacy.api.utils.http;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import no.nav.common.auth.context.AuthContext;
import no.nav.common.auth.context.AuthContextHolderThreadLocal;
import no.nav.common.auth.context.UserRole;
import no.nav.common.utils.fn.UnsafeRunnable;
import no.nav.common.utils.fn.UnsafeSupplier;

import static java.util.Collections.emptyMap;

public class SubjectHandlerUtil {

    public static <T> T withIdent(String ident, UnsafeSupplier<T> runnable) {
        AuthContext authcontext = new AuthContext(
                UserRole.INTERN,
                new PlainJWT(new JWTClaimsSet.Builder().subject(ident).build())
        );
        return AuthContextHolderThreadLocal.instance().withContext(authcontext, runnable);
    }

    public static void withIdent(String ident, UnsafeRunnable runnable) {
        AuthContext authcontext = new AuthContext(
                UserRole.INTERN,
                new PlainJWT(new JWTClaimsSet.Builder().subject(ident).build())
        );
        AuthContextHolderThreadLocal.instance().withContext(authcontext, runnable);
    }

}
