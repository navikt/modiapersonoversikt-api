package no.nav.modiapersonoversikt.legacy.api.utils.http;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import no.nav.common.auth.context.AuthContext;
import no.nav.common.auth.context.UserRole;
import no.nav.common.utils.fn.UnsafeRunnable;
import no.nav.common.utils.fn.UnsafeSupplier;
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils;


public class AuthContextTestUtils {

    public static <T> T withIdent(String ident, UnsafeSupplier<T> runnable) {
        AuthContext authcontext = new AuthContext(
                UserRole.INTERN,
                new PlainJWT(new JWTClaimsSet.Builder().subject(ident).build())
        );
        return AuthContextUtils.withContext(authcontext, runnable);
    }

    public static void withIdent(String ident, UnsafeRunnable runnable) {
        AuthContext authcontext = new AuthContext(
                UserRole.INTERN,
                new PlainJWT(new JWTClaimsSet.Builder().subject(ident).build())
        );
        AuthContextUtils.withContext(authcontext, runnable);
    }

}
