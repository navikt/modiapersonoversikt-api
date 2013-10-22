package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.endpoints;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.OppgavebehandlingPortType;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.security.SecurityConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static org.apache.cxf.frontend.ClientProxy.getClient;

@Configuration
public class OppgavebehandlingEndpointConfig {

    @Value("${oppgavebehandlingendpoint.url}")
    protected String oppgavebehandlingEndpoint;

    @Bean
    public OppgavebehandlingPortType oppgavebehandlingPortType() {
        return opprettOppgavebehandlingPortType(new UserSAMLOutInterceptor());
    }

    @Bean
    public Pingable oppgavebehandlingPing() {
        return new OppgavebehandlingPing(opprettOppgavebehandlingPortType(new SystemSAMLOutInterceptor()));
    }

    private OppgavebehandlingPortType opprettOppgavebehandlingPortType(AbstractSAMLOutInterceptor samlOutInterceptor) {
        JaxWsProxyFactoryBean factoryBean = commonJaxWsConfig(samlOutInterceptor);
        factoryBean.setServiceClass(OppgavebehandlingPortType.class);
        factoryBean.setAddress(oppgavebehandlingEndpoint);
        factoryBean.setWsdlURL("classpath:Oppgavebehandling.wsdl");
        OppgavebehandlingPortType oppgavebehandlingPortType = factoryBean.create(OppgavebehandlingPortType.class);

        Client client = getClient(oppgavebehandlingPortType);
        HTTPConduit conduit = (HTTPConduit) client.getConduit();
        TLSClientParameters params = new TLSClientParameters();
        params.setDisableCNCheck(true);
        conduit.setTlsClientParameters(params);

        return oppgavebehandlingPortType;
    }

    private JaxWsProxyFactoryBean commonJaxWsConfig(AbstractSAMLOutInterceptor samlOutInterceptor) {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        Map<String, Object> properties = new HashMap<>();
        properties.put("schema-validation-enabled", true);
        properties.put(SecurityConstants.MUST_UNDERSTAND, false);
        factoryBean.setProperties(properties);
        List<Feature> features = factoryBean.getFeatures();
        features.add(new LoggingFeature());
        features.add(new WSAddressingFeature());
        factoryBean.getOutInterceptors().add(samlOutInterceptor);
        return factoryBean;
    }

    private static class OppgavebehandlingPing implements Pingable {

        OppgavebehandlingPortType oppgavebehandlingPortType;

        public OppgavebehandlingPing(OppgavebehandlingPortType oppgavebehandlingPortType) {
            this.oppgavebehandlingPortType = oppgavebehandlingPortType;
        }

        @Override
        public List<PingResult> ping() {
            long start = currentTimeMillis();
            try {
                boolean ping = oppgavebehandlingPortType.ping();
                long timeElapsed = currentTimeMillis() - start;
                return asList(new PingResult("Oppgavebehandling_v1", ping ? SERVICE_OK : SERVICE_FAIL, timeElapsed));
            } catch (Exception e) {
                long timeElapsed = currentTimeMillis() - start;
                return asList(new PingResult("Oppgavebehandling_v1", SERVICE_FAIL, timeElapsed));
            }
        }
    }
}
