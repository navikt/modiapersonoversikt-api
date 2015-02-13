package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

import no.nav.modig.core.exception.ApplicationException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static java.lang.System.getProperty;
import static java.lang.reflect.Proxy.newProxyInstance;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.mockSetupErTillatt;

public final class InstanceSwitcher implements InvocationHandler {

    private final Object defaultInstance;
    private final Object alternative;
    private final String key;

    private <T> InstanceSwitcher(T defaultInstance, T alternative, String key) {
        this.defaultInstance = defaultInstance;
        this.alternative = alternative;
        this.key = key;
    }

    public static <T> T createSwitcher(T defaultInstance, T alternative, String key, Class<T> type) {

        if (!mockSetupErTillatt()) {
            return defaultInstance;
        }

        return (T) newProxyInstance(
                InstanceSwitcher.class.getClassLoader(),
                new Class[]{type},
                new InstanceSwitcher(defaultInstance, alternative, key)
        );
    }

    @Override
    @SuppressWarnings("PMD")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        method.setAccessible(true);
        try {
            if (getProperty(key, "false").equalsIgnoreCase("true")) {
                return method.invoke(alternative, args);
            }
            return method.invoke(defaultInstance, args);
        } catch (IllegalAccessException exception) {
            throw new ApplicationException("Problemer med invokering av metode", exception);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    public String getTargetClassName() {
        return alternative.getClass().getName().split("\\$")[0];
    }
}
