package no.nav.modiapersonoversikt.infrastructure.metrics;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProxyMetodeKall {
    private final Object object;
    private final Method method;
    private final Object[] args;

    public ProxyMetodeKall(Object object, Method method, Object[] args) {
        this.object = object;
        this.method = method;
        this.args = args;
    }

    public Object kallMetode() throws Throwable {
        try {
            return method.invoke(object, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
