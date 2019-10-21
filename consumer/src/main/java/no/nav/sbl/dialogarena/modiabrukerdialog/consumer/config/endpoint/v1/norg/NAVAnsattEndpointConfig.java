package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.UnpingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import java.lang.reflect.Proxy;
import java.util.function.Supplier;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.Utils.withProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NorgEndpointFelles.NORG_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NorgEndpointFelles.getSecurityProps;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GosysNavAnsattPortTypeMock.createGosysNavAnsattPortTypeMock;

@Configuration
public class NAVAnsattEndpointConfig {

    private static String address = System.getProperty("tjenestebuss.url") + "nav-cons-sak-gosys-3.0.0Web/sca/GOSYSNAVAnsattWSEXP";

    @Bean
    public GOSYSNAVansatt gosysNavAnsatt() {
        GOSYSNAVansatt prod = createGosysNavAnsattPortType();
        GOSYSNAVansatt mock = createGosysNavAnsattPortTypeMock();

        return createMetricsProxyWithInstanceSwitcher("NorgAnsatt", prod, mock, NORG_KEY, GOSYSNAVansatt.class);
    }

    private static GOSYSNAVansatt createGosysNavAnsattPortType() {
        return withProperty("disable.ssl.cn.check", "true", () -> new CXFClient<>(GOSYSNAVansatt.class)
                .address(address)
                .wsdl("classpath:nav-cons-sak-gosys-3.0.0_GOSYSNAVAnsattWSEXP.wsdl")
                .serviceName(new QName("http://nav-cons-sak-gosys-3.0.0/no/nav/inf/NAVansatt/Binding", "GOSYSNAVAnsattWSEXP_GOSYSNAVansattHttpService"))
                .endpointName(new QName("http://nav-cons-sak-gosys-3.0.0/no/nav/inf/NAVansatt/Binding", "GOSYSNAVAnsattWSEXP_GOSYSNAVansattHttpPort"))
                .timeout(10000, 30000)
                .withOutInterceptor(new WSS4JOutInterceptor(getSecurityProps()))
                .build()
        );
    }

    @Bean
    public Pingable gosysNavAnsattPingable() {
        return new UnpingableWebService("Norg - navansatt", address);
    }
}
