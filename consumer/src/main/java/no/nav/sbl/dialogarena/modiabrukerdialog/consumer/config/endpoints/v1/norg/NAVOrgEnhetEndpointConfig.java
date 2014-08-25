package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v1.norg;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
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
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GosysNavOrgEnhetPortTypeMock.createGosysNavOrgEnhetPortTypeMock;

@Configuration
public class NAVOrgEnhetEndpointConfig {

    @Bean
    public GOSYSNAVOrgEnhet gosysNavOrgEnhet() {
        return createSwitcher(createNavOrgEnhetPortType(), createGosysNavOrgEnhetPortTypeMock(), NORG_KEY, GOSYSNAVOrgEnhet.class);
    }

    private static GOSYSNAVOrgEnhet createNavOrgEnhetPortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();

        proxyFactoryBean.setWsdlLocation("classpath:nav-cons-sak-gosys-3.0.0_GOSYSNAVOrgEnhetWSEXP.wsdl");
        proxyFactoryBean.setAddress(System.getProperty("tjenestebuss.url") + "nav-cons-sak-gosys-3.0.0Web/sca/GOSYSNAVOrgEnhetWSEXP");
        proxyFactoryBean.setServiceName(new QName("http://nav-cons-sak-gosys-3.0.0/no/nav/inf/NAVOrgEnhet/Binding", "GOSYSNAVOrgEnhetWSEXP_GOSYSNAVOrgEnhetHttpService"));
        proxyFactoryBean.setEndpointName(new QName("http://nav-cons-sak-gosys-3.0.0/no/nav/inf/NAVOrgEnhet/Binding", "GOSYSNAVOrgEnhetWSEXP_GOSYSNAVOrgEnhetHttpPort"));
        proxyFactoryBean.setServiceClass(GOSYSNAVOrgEnhet.class);
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        proxyFactoryBean.getOutInterceptors().add(new WSS4JOutInterceptor(getSecurityProps()));

        GOSYSNAVOrgEnhet navOrgEnhet = proxyFactoryBean.create(GOSYSNAVOrgEnhet.class);

        HTTPConduit httpConduit = (HTTPConduit) ClientProxy.getClient(navOrgEnhet).getConduit();
        TLSClientParameters params = new TLSClientParameters();
        params.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(params);

        return navOrgEnhet;
    }
}
