package no.nav.modiapersonoversikt.legacy.api.utils.cache;

import no.nav.common.auth.context.AuthContextHolderThreadLocal;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Klasse for å lage cachekey som også tar høyde for
 * brukers ident, slik at ikke tilgang overstyres av
 * cachede entries.
 */
public class AutentisertBrukerKeyGenerator extends SimpleKeyGenerator {

    public AutentisertBrukerKeyGenerator() {
        super();
    }

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String cacheKey = Integer.toHexString(
                super.generate(target, method, params).hashCode()
        );
        return "user: " + getUser() + " cachekey: " + getTargetClassName(target) + "." + method.getName() + "[" + cacheKey + "]";
    }

    private String getUser() {
        return AuthContextHolderThreadLocal.instance().getSubject().orElse("uauthentisert");
    }

    private String getTargetClassName(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            return AopProxyUtils.proxiedUserInterfaces(target)[0].getName();
        }
        return target.getClass().getName();
    }
}
