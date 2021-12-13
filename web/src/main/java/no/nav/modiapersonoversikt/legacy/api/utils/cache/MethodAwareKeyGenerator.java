package no.nav.modiapersonoversikt.legacy.api.utils.cache;

import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.Method;

/**
 * Klasse for å lage cachekey som også tar høyde for methodens navn.
 */
public class MethodAwareKeyGenerator extends SimpleKeyGenerator {

    public MethodAwareKeyGenerator() {
        super();
    }

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return super.generate(target, method, method.getName(), params);

    }
}
