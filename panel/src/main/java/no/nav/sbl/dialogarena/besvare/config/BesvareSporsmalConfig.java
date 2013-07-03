package no.nav.sbl.dialogarena.besvare.config;

import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.HenvendelseSporsmalOgSvarPortType;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Import(JaxWsFeatures.Integration.class)
public class BesvareSporsmalConfig {

    @Inject
    private JaxWsFeatures jaxwsFeatures;

    @Value("${henvendelser.webservice.sporsmal.url}")
    protected String spmSvarEndpoint;

    @Bean
    public HenvendelseSporsmalOgSvarPortType sporsmalOgSvarService() {
        return henvendelseSporsmalOgSvarPortTypeFactory().create(HenvendelseSporsmalOgSvarPortType.class);
    }

    @Bean
    public JaxWsProxyFactoryBean henvendelseSporsmalOgSvarPortTypeFactory() {
        JaxWsProxyFactoryBean jaxwsClient = commonJaxWsConfig();
        jaxwsClient.setServiceClass(HenvendelseSporsmalOgSvarPortType.class);
        jaxwsClient.setAddress(spmSvarEndpoint);
        jaxwsClient.setWsdlURL(classpathUrl("HenvendelseSporsmalOgSvar.wsdl"));
        return jaxwsClient;
    }

    @Bean
    public JaxWsProxyFactoryBean commonJaxWsConfig() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        Map<String, Object> properties = new HashMap<>();
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