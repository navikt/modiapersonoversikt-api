package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Timer;
import no.nav.modig.core.exception.ApplicationException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.codahale.metrics.MetricRegistry.name;
import static java.lang.reflect.Proxy.newProxyInstance;
import static no.nav.modig.modia.metrics.MetricsFactory.registry;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;

public class TimingMetricsProxy implements InvocationHandler {
    private final Object object;
    private final Map<Method, Timer> methodTimers;
    private final Map<Method, AtomicLong> methodInvocationCounter;
    private final Map<Method, AtomicLong> methodErrorCounter;


    public TimingMetricsProxy(Object object, Class type) {
        this.object = object;
        this.methodTimers = new HashMap<>();
        this.methodInvocationCounter = new HashMap<>();
        this.methodErrorCounter = new HashMap<>();

        Method[] methods = type.getMethods();

        for (final Method method : methods) {
            Gauge<Long> errorGauge = new Gauge<Long>() {
                @Override
                public Long getValue() {
                    return methodErrorCounter.get(method).longValue();

                }
            };
            Gauge<Long> invocationGauge = new Gauge<Long>() {
                @Override
                public Long getValue() {
                    return methodInvocationCounter.get(method).longValue();
                }
            };
            Timer timer = registry.timer(name(type, method.getName(), "time"));

            registry.register(name(type, method.getName(), "errorPercent"), errorGauge);
            registry.register(name(type, method.getName(), "invocationCount"), invocationGauge);

            methodTimers.put(method, timer);
            methodErrorCounter.put(method, new AtomicLong());
            methodInvocationCounter.put(method, new AtomicLong());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T createMetricsProxy(T object, Class<T> type) {
        //Metrics need unique names. Creating two proxies of same type will therefore create an exception
        if (alreadyRegistered(type)) {
            return object;
        }

        return (T) newProxyInstance(
                TimingMetricsProxy.class.getClassLoader(),
                new Class[]{type},
                new TimingMetricsProxy(object, type)
        );
    }

    public static <T> T createMetricsProxyWithInstanceSwitcher(T prod, T mock, String key, Class<T> type) {
        return createMetricsProxy(createSwitcher(prod, mock, key, type), type);
    }

    private static <T> boolean alreadyRegistered(Class<T> type) {
        Method[] methods = type.getMethods();
        String methodName = "";
        if (methods.length > 0) {
            methodName = methods[0].getName();
        }
        return registry.getTimers().get(name(type, methodName, "time")) != null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        method.setAccessible(true);
        final Timer.Context context = methodTimers.get(method).time();
        try {
            return method.invoke(object, args);
        } catch (Exception e) {
            methodErrorCounter.get(method).incrementAndGet();
            throw new ApplicationException("Problemer med invokering av metode", e);
        } finally {
            context.stop();
            methodInvocationCounter.get(method).incrementAndGet();
        }
    }
}
