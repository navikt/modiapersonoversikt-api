package no.nav.modiapersonoversikt.config.endpoint.v1.norg;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import no.nav.common.cxf.CXFClient;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.infrastructure.ping.UnpingableWebService;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;
import static no.nav.modiapersonoversikt.config.endpoint.Utils.withProperty;
import static no.nav.modiapersonoversikt.config.endpoint.v1.norg.NorgEndpointFelles.getSecurityProps;

@Configuration
public class NAVOrgEnhetEndpointConfig {

    private static String address = EnvironmentUtils.getRequiredProperty("TJENESTEBUSS_URL") + "nav-cons-sak-gosys-3.0.0Web/sca/GOSYSNAVOrgEnhetWSEXP";

    @Bean
    public GOSYSNAVOrgEnhet gosysNavOrgEnhet() {
        GOSYSNAVOrgEnhet prod = createNavOrgEnhetPortType();

        return createTimerProxyForWebService("NorgEnhet", prod, GOSYSNAVOrgEnhet.class);
    }

    private static GOSYSNAVOrgEnhet createNavOrgEnhetPortType() {
        return withProperty("disable.ssl.cn.check", "true", () -> new CXFClient<>(GOSYSNAVOrgEnhet.class)
                .address(address)
                .wsdl("classpath:wsdl/tjenestespesifikasjon/nav-cons-sak-gosys-3.0.0_GOSYSNAVOrgEnhetWSEXP.wsdl")
                .serviceName(new QName("http://nav-cons-sak-gosys-3.0.0/no/nav/inf/NAVOrgEnhet/Binding", "GOSYSNAVOrgEnhetWSEXP_GOSYSNAVOrgEnhetHttpService"))
                .endpointName(new QName("http://nav-cons-sak-gosys-3.0.0/no/nav/inf/NAVOrgEnhet/Binding", "GOSYSNAVOrgEnhetWSEXP_GOSYSNAVOrgEnhetHttpPort"))
                .withOutInterceptor(new WSS4JOutInterceptor(getSecurityProps()))
                .build()
        );
    }

    @Bean
    public Pingable GOSYSNAVOrgEnhetPingable() {
        return new UnpingableWebService("Norg - org.enhet", address);
    }
}
