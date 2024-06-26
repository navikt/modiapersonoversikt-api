package no.nav.modiapersonoversiktproxy.infrastructure.metrics;

import static java.lang.reflect.Proxy.newProxyInstance;

public class MetricsFactory {
    public static <T> T createTimerProxyForWebService(String name, T object, Class<T> type) {
        return createTimerProxyInstance("ws." + name, object, type);
    }

    public static <T> T createTimerProxyInstance(String name, T object, Class<T> type) {
        ClassLoader classLoader = TimerProxy.class.getClassLoader();
        var classes = new Class[]{type};
        TimerProxy timerProxy = new TimerProxy(name, object);

        return (T) newProxyInstance(classLoader, classes, timerProxy);
    }
}
