package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.endpoints;

import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

@Configuration
public class SakOgBehandlingEndpointConfig {

    @Value("${sakogbehandling.url}")
    private URL sakogbehandlingEndpoint;

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        SakOgBehandlingPortType sakOgBehandlingPortType = createSakOgBehandlingPortType(new UserSAMLOutInterceptor());
        return sakOgBehandlingPortType;
    }

    @Bean
    public SakOgBehandlingPortType selfTestSakOgBehandlingPortType() {
        SakOgBehandlingPortType sakOgBehandlingPortType = createSakOgBehandlingPortType(new SystemSAMLOutInterceptor());
        return sakOgBehandlingPortType;
    }


    private SakOgBehandlingPortType createSakOgBehandlingPortType(AbstractSAMLOutInterceptor interceptor) {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("sakOgBehandling/no/nav/tjeneste/virksomhet/sakOgBehandling/v1/SakOgBehandling.wsdl");
        proxyFactoryBean.setAddress(sakogbehandlingEndpoint.toString());
        proxyFactoryBean.setServiceClass(SakOgBehandlingPortType.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        return proxyFactoryBean.create(SakOgBehandlingPortType.class);
    }
}
