package no.nav.metrics;

import no.nav.common.metrics.InfluxClient;
import no.nav.common.metrics.MetricsClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimerProxy implements InvocationHandler {
    private static final MetricsClient client = new InfluxClient();
    private static final Timing timing = new Timing() {};
    private static final List<String> DO_NOT_MEASURE_METHOD_NAMES = new ArrayList<>(Arrays.asList("hashCode", "equals", "toString"));
    private final String name;
    private final Object originalObject;


    public TimerProxy(String name, Object originalObject) {
        this.name = name;
        this.originalObject = originalObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] objects) throws Throwable {
        if (DO_NOT_MEASURE_METHOD_NAMES.contains(method.getName())) {
            return method.invoke(originalObject, objects);
        }

        String timerName = name + "." + method.getName();
        Timer timer = new Timer(client, timerName, timing);
        timer.start();
        try {
            return method.invoke(originalObject, objects);
        } catch (RuntimeException | Error unchecked) {
            timer.setFailed();
            timer.addFieldToReport("checkedException", false);
            throw unchecked;
        } catch (Throwable checked) {
            timer.setFailed();
            timer.addFieldToReport("checkedException", true);
            throw checked;
        } finally {
            timer.stop().report();
        }
    }
}
