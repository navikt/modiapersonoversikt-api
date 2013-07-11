package no.nav.sbl.dialogarena.besvare.config;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfig {

    @Inject
    private JaxWsFeatures jaxwsFeatures;

    @Value("${spormalogsvarendpoint.url}")
    protected String spmSvarEndpoint;

    @Bean
    public SporsmalOgSvarPortType sporsmalOgSvarPortType() {
        SporsmalOgSvarPortType hnvSpsmSvarPortType = henvendelseSporsmalOgSvarPortTypeFactory().create(SporsmalOgSvarPortType.class);
        Client client = ClientProxy.getClient(hnvSpsmSvarPortType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        httpConduit.setTlsClientParameters(jaxwsFeatures.tlsClientParameters());
        STSConfigurationUtility.configureStsForSystemUser(ClientProxy.getClient(hnvSpsmSvarPortType));
        return hnvSpsmSvarPortType;
    }

    @Bean
    public JaxWsProxyFactoryBean henvendelseSporsmalOgSvarPortTypeFactory() {
        JaxWsProxyFactoryBean jaxwsClient = commonJaxWsConfig();
        jaxwsClient.setServiceClass(SporsmalOgSvarPortType.class);
        jaxwsClient.setAddress(spmSvarEndpoint);
        jaxwsClient.setWsdlURL(classpathUrl("HenvendelseSporsmalOgSvar.wsdl"));
        return jaxwsClient;
    }

    @Bean
    public JaxWsProxyFactoryBean commonJaxWsConfig() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("schema-validation-enabled", true);
        factoryBean.setProperties(properties);
        factoryBean.getFeatures().addAll(jaxwsFeatures.jaxwsFeatures());
        return factoryBean;
    }

    private String classpathUrl(String classpathLocation) {
        if (getClass().getClassLoader().getResource(classpathLocation) == null) {
            throw new RuntimeException(classpathLocation + " does not exist on classpath!");
        }
        return "classpath:" + classpathLocation;
    }
}