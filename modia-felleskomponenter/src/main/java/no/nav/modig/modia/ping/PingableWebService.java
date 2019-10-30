package no.nav.modig.modia.ping;

import org.apache.cxf.jaxws.JaxWsClientProxy;
import org.apache.cxf.service.model.EndpointInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.stream.Stream;

public class PingableWebService implements Pingable {

    private final String name;
    private final Object webservice;

    public PingableWebService(String name, Object webservice) {
        assert name.length() > 0;
        this.name = name;
        this.webservice = webservice;
    }

    @Override
    public PingResult ping() {
        Method[] methods = webservice.getClass().getMethods();
        Optional<Method> optionalMethod = Stream.of(methods)
                .filter((method) -> "ping".equalsIgnoreCase(method.getName()))
                .findFirst();

        Method ping = optionalMethod.orElseThrow(() ->
                new UnsupportedOperationException("Webservicen har ikke en ping-metode. Implementer en selv via interface."));

        long start = System.currentTimeMillis();
        try {
            ping.invoke(webservice);
            return new OkPingResult(System.currentTimeMillis() - start);
        } catch (Exception e) {
            return new FailedPingResult(e.getCause(), System.currentTimeMillis() - start);
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String method() {
        return "ping";
    }

    @Override
    public String endpoint() {
        Proxy proxy = (Proxy) this.webservice;
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(proxy);
        EndpointInfo endpointInfo = ((JaxWsClientProxy) invocationHandler).getClient().getEndpoint().getEndpointInfo();
        return new StringBuilder()
                .append(endpointInfo.getName().getNamespaceURI())
                .append("/")
                .append(endpointInfo.getName().getLocalPart())
                .append(" via ")
                .append(endpointInfo.getAddress())
                .toString();
    }
}
