package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import no.nav.common.cxf.CXFClient;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modig.modia.ping.UnpingableWebService;
import no.nav.sbl.dialogarena.types.Pingable;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.xml.namespace.QName;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.Utils.withProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NorgEndpointFelles.getSecurityProps;

@Configuration
public class NAVAnsattEndpointConfig {

    private static String address = EnvironmentUtils.getRequiredProperty("TJENESTEBUSS_URL") + "nav-cons-sak-gosys-3.0.0Web/sca/GOSYSNAVAnsattWSEXP";

    @Bean
    public GOSYSNAVansatt gosysNavAnsatt() {
        GOSYSNAVansatt prod = createGosysNavAnsattPortType();

        return createTimerProxyForWebService("NorgAnsatt", prod, GOSYSNAVansatt.class);
    }

    private static GOSYSNAVansatt createGosysNavAnsattPortType() {
        return withProperty("disable.ssl.cn.check", "true", () -> new CXFClient<>(GOSYSNAVansatt.class)
                .address(address)
                .wsdl("classpath:wsdl/tjenestespesifikasjon/nav-cons-sak-gosys-3.0.0_GOSYSNAVAnsattWSEXP.wsdl")
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
