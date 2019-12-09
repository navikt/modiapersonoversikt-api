package no.nav.modig.modia.ping;

import no.nav.sbl.dialogarena.types.Pingable;
import org.apache.cxf.jaxws.JaxWsClientProxy;
import org.apache.cxf.service.model.EndpointInfo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.stream.Stream;

public class PingableWebService implements Pingable {
    private final ConsumerPingable delegate;

    public PingableWebService(String name, Object webservice) {
        this(name, false, webservice);
    }

    public PingableWebService(String name, boolean kritisk, Object webservice) {
        assert name.length() > 0;
        Method[] methods = webservice.getClass().getMethods();
        Optional<Method> optionalMethod = Stream.of(methods)
                .filter((method) -> "ping".equalsIgnoreCase(method.getName()))
                .findFirst();

        Method method = optionalMethod.orElseThrow(() ->
                new UnsupportedOperationException("Webservicen har ikke en ping-metode. Implementer en selv via interface."));

        Ping.PingMetadata metadata = new Ping.PingMetadata(
                webservice.getClass().getName(),
                endepunkt(webservice),
                name,
                kritisk
        );

        delegate = new ConsumerPingable(
                metadata,
                () -> method.invoke(webservice)
        );
    }

    @Override
    public Ping ping() {
        return delegate.ping();
    }

    private String endepunkt(Object webservice) {
        try {
            EndpointInfo endpointInfo = getProxy(webservice).getClient().getEndpoint().getEndpointInfo();
            return new StringBuilder()
                    .append(endpointInfo.getName().getNamespaceURI())
                    .append("/")
                    .append(endpointInfo.getName().getLocalPart())
                    .append(" via ")
                    .append(endpointInfo.getAddress())
                    .toString();

        } catch (Exception e) {
            return "Not recognized proxy: " + webservice.getClass().getSimpleName();
        }
    }

    private JaxWsClientProxy getProxy(Object object) throws Exception {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(object);
        if (invocationHandler instanceof JaxWsClientProxy) {
            return (JaxWsClientProxy)invocationHandler;
        }

        // package-protected klasse så derfor en liten hack her
        if ("no.nav.sbl.dialogarena.common.cxf.CXFClientInvocationHandler".equals(invocationHandler.getClass().getName())) {
            Field invokationHandler = invocationHandler.getClass().getDeclaredField("invocationHandler");
            invokationHandler.setAccessible(true);
            Object lambda = invokationHandler.get(invocationHandler);

            Field nestedHandler = lambda.getClass().getDeclaredField("arg$1");
            nestedHandler.setAccessible(true);
            Object clientProxy = nestedHandler.get(lambda);
            Object value = Proxy.getInvocationHandler(clientProxy);

            if (value instanceof JaxWsClientProxy) {
                return (JaxWsClientProxy)value;
            }
        }

        throw new IllegalArgumentException("Unknown class: " + object.getClass());
    }
}
