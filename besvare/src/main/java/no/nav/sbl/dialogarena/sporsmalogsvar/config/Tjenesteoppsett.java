package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.security.SecurityConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Tjenesteoppsett {

    @Inject
    private JaxWsFeatures jaxwsFeatures;

    protected String besvareHenvendelseEndpoint = "http://localhost:8686/besvarehenvendelse/services/domene.Brukerdialog/BesvareHenvendelseService_v1?wsdl";

    @Bean
    public Pingable spmOgSvarPing() {
        return new BesvareHenvendelsePingImpl(besvareSystemUser());
    }

    @Bean
    public BesvareHenvendelsePortType besvareHenvendelsePortType() {
        return opprettBesvareHenvendelsePortType(new UserSAMLOutInterceptor());
    }

    @Bean
    public BesvareHenvendelsePortType besvareSystemUser() {
        return opprettBesvareHenvendelsePortType(new SystemSAMLOutInterceptor());
    }

    private BesvareHenvendelsePortType opprettBesvareHenvendelsePortType(AbstractSAMLOutInterceptor samlOutInterceptor) {
        JaxWsProxyFactoryBean jaxwsClient = commonJaxWsConfig(samlOutInterceptor);
        jaxwsClient.setServiceClass(BesvareHenvendelsePortType.class);
        jaxwsClient.setAddress(besvareHenvendelseEndpoint);
        jaxwsClient.setWsdlURL("classpath:v1/BesvareHenvendelse.wsdl");
        BesvareHenvendelsePortType besvareHenvendelsePortType = jaxwsClient.create(BesvareHenvendelsePortType.class);
        return konfigurerMedHttps(besvareHenvendelsePortType);

    }

    private <T> T konfigurerMedHttps(T portType) {
        Client client = ClientProxy.getClient(portType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        httpConduit.setTlsClientParameters(jaxwsFeatures.tlsClientParameters());
        return portType;
	}

    private JaxWsProxyFactoryBean commonJaxWsConfig(AbstractSAMLOutInterceptor samlOutInterceptor) {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        Map<String, Object> properties = new HashMap<>();
        properties.put("schema-validation-enabled", true);
        properties.put(SecurityConstants.MUSTUNDERSTAND, false);
        factoryBean.setProperties(properties);
        factoryBean.getFeatures().addAll(jaxwsFeatures.jaxwsFeatures());
        factoryBean.getOutInterceptors().add(samlOutInterceptor);
        return factoryBean;
    }

}