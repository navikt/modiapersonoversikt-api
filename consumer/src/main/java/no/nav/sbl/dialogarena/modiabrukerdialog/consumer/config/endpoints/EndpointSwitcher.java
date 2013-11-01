package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.ConfigUtil.isInMockMode;

public class EndpointSwitcher implements InvocationHandler {
    private final Object portType;
    private final Object portTypeMock;
    private final String key;

    public <T> EndpointSwitcher(T portType, T portTypeMock, String key) {
        this.portType = portType;
        this.portTypeMock = portTypeMock;
        this.key = key;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (isInMockMode(key)) {
            return method.invoke(portTypeMock, args);
        }
        return method.invoke(portType, args);
    }
}
