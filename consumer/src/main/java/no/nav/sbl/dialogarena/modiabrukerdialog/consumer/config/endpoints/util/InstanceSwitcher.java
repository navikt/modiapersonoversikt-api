package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static java.lang.System.getProperty;
import static java.lang.reflect.Proxy.newProxyInstance;

public class InstanceSwitcher implements InvocationHandler {

    private final Object defaultInstance;
    private final Object alternative;
    private final String key;

    private <T> InstanceSwitcher(T defaultInstance, T alternative, String key) {
        this.defaultInstance = defaultInstance;
        this.alternative = alternative;
        this.key = key;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String useDefault = getProperty(key, "no");
        method.setAccessible(true);
        if (useDefault.equalsIgnoreCase("yes")) {
            return method.invoke(alternative, args);
        }
        return method.invoke(defaultInstance, args);
    }

    public static  <T> T createSwitcher(T defaultInstance, T alternative, String key, Class<T> type) {
        return (T) newProxyInstance(
                InstanceSwitcher.class.getClassLoader(),
                new Class[]{type},
                new InstanceSwitcher(defaultInstance, alternative, key)
        );
    }

}
