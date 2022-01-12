package no.nav.modiapersonoversikt.legacy.api.utils.cache;

import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
