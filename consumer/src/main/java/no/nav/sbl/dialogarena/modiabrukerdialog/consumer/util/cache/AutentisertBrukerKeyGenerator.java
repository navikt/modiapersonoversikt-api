package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

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
        return getSubjectHandler().getUid();
    }

    private String getTargetClassName(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(target);
            if (invocationHandler instanceof InstanceSwitcher) {
                return ((InstanceSwitcher) invocationHandler).getTargetClassName();
            } else {
                return AopProxyUtils.proxiedUserInterfaces(target)[0].getName();
            }
        }
        return target.getClass().getName();
    }
}
