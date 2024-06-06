package no.nav.modiapersonoversiktproxy.infrastructure.cache;

import no.nav.modiapersonoversiktproxy.infrastructure.AuthContextUtils;

import java.lang.reflect.Method;

/**
 * Klasse for å lage cachekey som også tar høyde for
 * brukers ident, slik at ikke tilgang overstyres av
 * cachede entries.
 */
public class AutentisertBrukerKeyGenerator extends MethodAwareKeyGenerator {

    public AutentisertBrukerKeyGenerator() {
        super();
    }

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String cacheKey = ((String) super.generate(target, method, params));
        return "user: " + getUser() + " " + cacheKey;
    }

    private String getUser() {
        return AuthContextUtils.getIdent().orElse("uauthentisert");
    }
}
