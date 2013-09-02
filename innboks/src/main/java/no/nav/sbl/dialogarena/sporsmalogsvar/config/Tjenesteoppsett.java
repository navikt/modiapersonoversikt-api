package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.security.SecurityConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Tjenesteoppsett {

    @Inject
    private JaxWsFeatures jaxwsFeatures;

    @Value("${spormalogsvarendpoint.url}")
    protected String spmSvarEndpoint;

    @Value("${henvendelseendpoint.url}")
    protected String henvendelseEndpoint;

    @Bean
    public Pingable spmOgSvarPing() {
        return new SpmOgSvarPingImpl(henvendelseSystemUser());
    }

    @Bean
    public MeldingService meldingService() {
        return new MeldingService.Default(henvendelseSso());
    }

    @Bean
    public HenvendelsePortType henvendelseSso() {
        return opprettHenvendelsePortType(new UserSAMLOutInterceptor());
    }

    @Bean
    public HenvendelsePortType henvendelseSystemUser() {
        return opprettHenvendelsePortType(new SystemSAMLOutInterceptor());
    }

    private HenvendelsePortType opprettHenvendelsePortType(AbstractSAMLOutInterceptor samlOutInterceptor) {
    	JaxWsProxyFactoryBean jaxwsClient = commonJaxWsConfig(samlOutInterceptor);
    	jaxwsClient.setServiceClass(HenvendelsePortType.class);
    	jaxwsClient.setAddress(henvendelseEndpoint);
    	jaxwsClient.setWsdlURL("classpath:Henvendelse.wsdl");
    	HenvendelsePortType henvendelsePortType = jaxwsClient.create(HenvendelsePortType.class);
    	return konfigurerMedHttps(henvendelsePortType);
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