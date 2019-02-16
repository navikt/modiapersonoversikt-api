package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.UnpingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.util.EnvironmentUtils;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NorgEndpointFelles.NORG_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NorgEndpointFelles.getSecurityProps;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GosysNavOrgEnhetPortTypeMock.createGosysNavOrgEnhetPortTypeMock;

@Configuration
public class NAVOrgEnhetEndpointConfig {

    private static String address = System.getProperty("tjenestebuss.url") + "nav-cons-sak-gosys-3.0.0Web/sca/GOSYSNAVOrgEnhetWSEXP";

    @Bean
    public GOSYSNAVOrgEnhet gosysNavOrgEnhet() {
        GOSYSNAVOrgEnhet prod = createNavOrgEnhetPortType();
        GOSYSNAVOrgEnhet mock = createGosysNavOrgEnhetPortTypeMock();

        return createMetricsProxyWithInstanceSwitcher("NorgEnhet", prod, mock, NORG_KEY, GOSYSNAVOrgEnhet.class);
    }

    private static GOSYSNAVOrgEnhet createNavOrgEnhetPortType() {
        GOSYSNAVOrgEnhet navOrgEnhet = new CXFClient<>(GOSYSNAVOrgEnhet.class)
                .address(address)
                .wsdl("classpath:nav-cons-sak-gosys-3.0.0_GOSYSNAVOrgEnhetWSEXP.wsdl")
                .serviceName(new QName("http://nav-cons-sak-gosys-3.0.0/no/nav/inf/NAVOrgEnhet/Binding", "GOSYSNAVOrgEnhetWSEXP_GOSYSNAVOrgEnhetHttpService"))
                .endpointName(new QName("http://nav-cons-sak-gosys-3.0.0/no/nav/inf/NAVOrgEnhet/Binding", "GOSYSNAVOrgEnhetWSEXP_GOSYSNAVOrgEnhetHttpPort"))
                .withOutInterceptor(new WSS4JOutInterceptor(getSecurityProps()))
                .build();

        HTTPConduit httpConduit = (HTTPConduit) ClientProxy.getClient(navOrgEnhet).getConduit();
        TLSClientParameters params = new TLSClientParameters();
        params.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(params);

        return navOrgEnhet;
    }

    @Bean
    public Pingable GOSYSNAVOrgEnhetPingable() {
        return new UnpingableWebService("Norg - org.enhet", address);
    }
}
