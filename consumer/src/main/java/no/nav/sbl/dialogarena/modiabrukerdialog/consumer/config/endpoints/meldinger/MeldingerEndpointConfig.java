package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.meldinger;

import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.HenvendelseMeldingerPortType;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeldingerEndpointConfig {

    @Bean
    public HenvendelseMeldingerPortType henvendelseMeldingerPortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("classpath:no/nav/tjeneste/domene/brukerdialog/henvendelsemeldinger/v1/Meldinger.wsdl");
        proxyFactoryBean.setAddress("https://localhost:8443/henvendelse/services/domene.Brukerdialog/HenvendelseMeldingerService_v1");
        proxyFactoryBean.setServiceClass(HenvendelseMeldingerPortType.class);
        proxyFactoryBean.getOutInterceptors().add(new UserSAMLOutInterceptor());
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        HenvendelseMeldingerPortType portType = proxyFactoryBean.create(HenvendelseMeldingerPortType.class);
        Client client = ClientProxy.getClient(portType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        TLSClientParameters clientParameters = new TLSClientParameters();
        clientParameters.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(clientParameters);
        return portType;
    }

}
