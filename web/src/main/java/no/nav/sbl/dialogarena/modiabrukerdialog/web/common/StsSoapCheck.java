package no.nav.sbl.dialogarena.modiabrukerdialog.web.common;

import no.nav.common.cxf.STSConfigurationUtil;
import no.nav.common.cxf.StsConfig;
import no.nav.common.health.HealthCheck;
import no.nav.common.health.HealthCheckResult;
import no.nav.common.health.selftest.SelfTestCheck;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.ClientImpl;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.endpoint.EndpointImpl;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.ServiceImpl;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.apache.cxf.ws.policy.EndpointPolicy;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.neethi.PolicyComponent;
import org.apache.neethi.PolicyContainingAssertion;
import org.apache.neethi.PolicyOperator;
import org.apache.wss4j.policy.model.IssuedToken;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.stream.Stream;

public class StsSoapCheck implements HealthCheck {
    private static StsSoapCheck INSTANCE;
    private final Bus bus = BusFactory.getDefaultBus();
    private final PolicyEngine policyEngine = bus.getExtension(PolicyEngine.class);
    private final EndpointInfo endpointInfo = dummyEndpointInfo();
    private final ClientImpl client = dummyClient();
    private final MessageImpl message = dummyMessage();
    private final STSClient stsClient;

    private StsSoapCheck(StsConfig stsConfig) {
        this.stsClient = stsClient(stsConfig);
    }

    public static StsSoapCheck getInstance(StsConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new StsSoapCheck(config);
        }
        return INSTANCE;
    }

    private EndpointInfo dummyEndpointInfo() {
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setInterface(new InterfaceInfo(serviceInfo, null));
        EndpointInfo endpointInfo = new EndpointInfo(serviceInfo, HTTPTransportFactory.DEFAULT_NAMESPACES.get(0));
        endpointInfo.setName(new QName("dummy"));
        return endpointInfo;
    }

    private ClientImpl dummyClient()  {
        try {
            ServiceImpl service = new ServiceImpl();
            return new ClientImpl(this.bus, new EndpointImpl(this.bus, service, this.endpointInfo));
        } catch (EndpointException e) {
            throw new RuntimeException(e);
        }
    }

    private MessageImpl dummyMessage() {
        MessageImpl message = new MessageImpl();
        ExchangeImpl exchange = new ExchangeImpl();
        exchange.put(Bus.class, this.bus);
        message.setExchange(exchange);
        return message;
    }

    private STSClient stsClient(StsConfig stsConfig) {
        STSConfigurationUtil.configureStsForSystemUserInFSS(this.client, stsConfig);
        STSClient stsClient = (STSClient)client.getRequestContext().values().iterator().next();
        stsClient.setMessage(this.message);
        stsClient.setTemplate(getRequestSecurityTokenTemplate());
        return stsClient;
    }

    private Element getRequestSecurityTokenTemplate() {
        EndpointPolicy clientEndpointPolicy = policyEngine.getClientEndpointPolicy(this.endpointInfo, client.getConduit(), null);
        return findAll(clientEndpointPolicy.getPolicy())
                .filter(IssuedToken.class::isInstance)
                .map(IssuedToken.class::cast)
                .map(IssuedToken::getRequestSecurityTokenTemplate)
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    private Stream<PolicyComponent> findAll(PolicyComponent policy) {
        return Stream.concat(Stream.of(policy), subComponents(policy));
    }

    private Stream<PolicyComponent> subComponents(PolicyComponent policy) {
        if (policy instanceof PolicyOperator) {
            return ((PolicyOperator) policy).getPolicyComponents().stream().flatMap(this::findAll);
        } else if (policy instanceof PolicyContainingAssertion) {
            return findAll(((PolicyContainingAssertion)policy).getPolicy());
        } else {
            return Stream.empty();
        }
    }

    @Override
    public HealthCheckResult checkHealth() {
        try {
            this.stsClient.requestSecurityToken();
            return HealthCheckResult.healthy();
        } catch (Exception e) {
            return HealthCheckResult.unhealthy(e);
        }
    }

    public static SelfTestCheck asSelftestCheck(StsConfig stsConfig) {
        return new SelfTestCheck(
                "Sjekker at systembruker kan hente token fra STS",
                true,
                getInstance(stsConfig)
        );
    }
}
