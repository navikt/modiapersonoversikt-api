package no.nav.sbl.dialogarena.saksoversikt.service.utils;

import no.nav.modig.core.exception.ApplicationException;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.lang.System.getProperty;
import static java.lang.reflect.Proxy.newProxyInstance;

public final class InstanceSwitcher implements InvocationHandler {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(InstanceSwitcher.class);
    private final Object defaultInstance;
    private final Object alternative;
    private final String key;

    private <T> InstanceSwitcher(T defaultInstance, T alternative, String key) {
        this.defaultInstance = defaultInstance;
        this.alternative = alternative;
        this.key = key;
    }

    public static <T> T createSwitcher(T defaultInstance, T alternative, String key, Class<T> type) {

        if (!MockUtil.mockSetupErTillatt()) {
            return defaultInstance;
        }

        return (T) newProxyInstance(
                InstanceSwitcher.class.getClassLoader(),
                new Class[]{type},
                new InstanceSwitcher(defaultInstance, alternative, key)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        method.setAccessible(true);
        try {
            if (getProperty(key, "false").equalsIgnoreCase("true")) {
                return method.invoke(alternative, args);
            }
            return method.invoke(defaultInstance, args);
        } catch(InvocationTargetException exception){
            LOG.info("invokasjon feiler, kaster reell exception", exception);
            throw exception.getCause();
        }catch (IllegalAccessException exception) {
            throw new ApplicationException("Problemer med invokering av metode", exception);
        }
    }
    public String getTargetClassName() {
        return alternative.getClass().getName().split("\\$")[0];
    }
}
