package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InstanceSwitcher implements InvocationHandler {
    private final Object defaultInstance;
    private final Object alternative;
    private final String key;

    public <T> InstanceSwitcher(T defaultInstance, T alternative, String key) {
        this.defaultInstance = defaultInstance;
        this.alternative = alternative;
        this.key = key;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String useAlterantive = System.getProperty(key, "no");

        if (!useAlterantive.equalsIgnoreCase("no")) {
            return method.invoke(alternative, args);
        }
        return method.invoke(defaultInstance, args);
    }

    public static  <T> T createSwitcher(T defaultInstance, T alternative, String key, Class<T> type) {
        return (T) Proxy.newProxyInstance(InstanceSwitcher.class.getClassLoader(),
                new Class[]{type}, new InstanceSwitcher(defaultInstance, alternative, key));
    }

}
