package no.nav.modiapersonoversikt.testutils;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import no.nav.common.auth.context.AuthContext;
import no.nav.common.auth.context.UserRole;
import no.nav.common.utils.fn.UnsafeRunnable;
import no.nav.common.utils.fn.UnsafeSupplier;
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils;

import static no.nav.common.auth.Constants.AAD_NAV_IDENT_CLAIM;


public class AuthContextTestUtils {

    public static <T> T withIdent(String ident, UnsafeSupplier<T> runnable) {
        AuthContext authcontext = new AuthContext(
                UserRole.INTERN,
                new PlainJWT(new JWTClaimsSet.Builder().subject(ident).claim(AAD_NAV_IDENT_CLAIM, ident).build())
        );
        return AuthContextUtils.withContext(authcontext, runnable);
    }

    public static void withIdent(String ident, UnsafeRunnable runnable) {
        AuthContext authcontext = new AuthContext(
                UserRole.INTERN,
                new PlainJWT(new JWTClaimsSet.Builder().subject(ident).claim(AAD_NAV_IDENT_CLAIM, ident).build())
        );
        AuthContextUtils.withContext(authcontext, runnable);
    }

}
