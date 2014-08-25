package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v1.norg;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v1.norg.NorgEndpointFelles.NORG_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v1.norg.NorgEndpointFelles.getSecurityProps;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GosysNavAnsattPortTypeMock.createGosysNavAnsattPortTypeMock;

@Configuration
public class NAVAnsattEndpointConfig {

    @Bean
    public GOSYSNAVansatt gosysNavAnsatt() {
        return createSwitcher(createGosysNavAnsattPortType(), createGosysNavAnsattPortTypeMock(), NORG_KEY, GOSYSNAVansatt.class);
    }

    private static GOSYSNAVansatt createGosysNavAnsattPortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();

        proxyFactoryBean.setWsdlLocation("classpath:nav-cons-sak-gosys-3.0.0_GOSYSNAVAnsattWSEXP.wsdl");
        proxyFactoryBean.setAddress(System.getProperty("tjenestebuss.url") + "nav-cons-sak-gosys-3.0.0Web/sca/GOSYSNAVAnsattWSEXP");
        proxyFactoryBean.setServiceName(new QName("http://nav-cons-sak-gosys-3.0.0/no/nav/inf/NAVansatt/Binding", "GOSYSNAVAnsattWSEXP_GOSYSNAVansattHttpService"));
        proxyFactoryBean.setEndpointName(new QName("http://nav-cons-sak-gosys-3.0.0/no/nav/inf/NAVansatt/Binding", "GOSYSNAVAnsattWSEXP_GOSYSNAVansattHttpPort"));
        proxyFactoryBean.setServiceClass(GOSYSNAVansatt.class);
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        proxyFactoryBean.getOutInterceptors().add(new WSS4JOutInterceptor(getSecurityProps()));

        GOSYSNAVansatt gosysnaVansatt = proxyFactoryBean.create(GOSYSNAVansatt.class);

        HTTPConduit httpConduit = (HTTPConduit) ClientProxy.getClient(gosysnaVansatt).getConduit();
        TLSClientParameters params = new TLSClientParameters();
        params.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(params);

        return gosysnaVansatt;
    }
}
