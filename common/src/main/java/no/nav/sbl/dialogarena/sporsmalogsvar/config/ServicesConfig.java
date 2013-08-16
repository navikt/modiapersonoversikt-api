package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.security.SecurityConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ServicesConfig {

    @Inject
    private JaxWsFeatures jaxwsFeatures;

    @Value("${spormalogsvarendpoint.url}")
    protected String spmSvarEndpoint;
    
    @Value("${henvendelseendpoint.url}")
    protected String henvendelseEndpoint;

    @Bean
    public Pingable spmOgSvarPing() {
        return new SpmOgSvarPingImpl(selftestSporsmalOgSvarPortType(), selftestHenvendelsePortType());
    }

    @Bean
    public MeldingService meldingService(HenvendelsePortType henvendelse, SporsmalOgSvarPortType sospt) {
        return new MeldingService(henvendelse, sospt);
    }

    @Bean
    public HenvendelsePortType henvendelsePortType() {
        return opprettHenvendelsePortType(new UserSAMLOutInterceptor());
    }

    @Bean
    public HenvendelsePortType selftestHenvendelsePortType() {
        return opprettHenvendelsePortType(new SystemSAMLOutInterceptor());
    }

    public HenvendelsePortType opprettHenvendelsePortType(AbstractSAMLOutInterceptor samlOutInterceptor) {
    	JaxWsProxyFactoryBean jaxwsClient = commonJaxWsConfig();
    	jaxwsClient.setServiceClass(HenvendelsePortType.class);
    	jaxwsClient.setAddress(henvendelseEndpoint);
    	jaxwsClient.setWsdlURL("classpath:Henvendelse.wsdl");
        jaxwsClient.getProperties().put(SecurityConstants.MUSTUNDERSTAND, false);
        jaxwsClient.getOutInterceptors().add(samlOutInterceptor);
    	HenvendelsePortType henvendelsePortType = jaxwsClient.create(HenvendelsePortType.class);
    	return konfigurerMedHttps(henvendelsePortType);
    }
    
    @Bean
    public SporsmalOgSvarPortType sporsmalOgSvarPortType() {
        return opprettSporsmalOgSvarPortType(new UserSAMLOutInterceptor());
    }

    @Bean
    public SporsmalOgSvarPortType selftestSporsmalOgSvarPortType() {
        return opprettSporsmalOgSvarPortType(new SystemSAMLOutInterceptor());
    }

    public SporsmalOgSvarPortType opprettSporsmalOgSvarPortType(AbstractSAMLOutInterceptor samlOutInterceptor) {
    	JaxWsProxyFactoryBean jaxwsClient = commonJaxWsConfig();
    	jaxwsClient.setServiceClass(SporsmalOgSvarPortType.class);
    	jaxwsClient.setAddress(spmSvarEndpoint);
    	jaxwsClient.setWsdlURL("classpath:SporsmalOgSvar.wsdl");
        jaxwsClient.getProperties().put(SecurityConstants.MUSTUNDERSTAND, false);
        jaxwsClient.getOutInterceptors().add(samlOutInterceptor);
    	SporsmalOgSvarPortType hnvSpsmSvarPortType = jaxwsClient.create(SporsmalOgSvarPortType.class);
    	return konfigurerMedHttps(hnvSpsmSvarPortType);
    }
    
    private <T> T konfigurerMedHttps(T portType) {
        Client client = ClientProxy.getClient(portType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        httpConduit.setTlsClientParameters(jaxwsFeatures.tlsClientParameters());
        return portType;
	}
    
    public JaxWsProxyFactoryBean commonJaxWsConfig() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        Map<String, Object> properties = new HashMap<>();
        properties.put("schema-validation-enabled", true);
        factoryBean.setProperties(properties);
        factoryBean.getFeatures().addAll(jaxwsFeatures.jaxwsFeatures());
        return factoryBean;
    }

}