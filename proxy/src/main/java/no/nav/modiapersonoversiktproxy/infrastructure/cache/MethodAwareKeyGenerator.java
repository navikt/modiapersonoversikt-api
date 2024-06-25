package no.nav.modiapersonoversiktproxy.infrastructure.cache;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Klasse for å lage cachekey som også tar høyde for methodens navn.
 */
public class MethodAwareKeyGenerator extends SimpleKeyGenerator {

    public MethodAwareKeyGenerator() {
        super();
    }

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String cacheKey = Integer.toHexString(super.generate(target, method, params).hashCode());
        return "cachekey: " + getTargetClassName(target) + "." + method.getName() + "[" + cacheKey + "]";
    }

    private String getTargetClassName(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            return AopProxyUtils.proxiedUserInterfaces(target)[0].getName();
        }
        return target.getClass().getName();
    }
}
