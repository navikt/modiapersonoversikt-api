package no.nav.sykmeldingsperioder.config.spring.foreldrepenger;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.metrics.MetricsFactory;
import no.nav.modig.jaxws.handlers.MDCOutHandler;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.ForeldrepengerV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Configuration
public class ForeldrepengerConsumerConfig {

    @Value("${VIRKSOMHET_FORELDREPENGER_V2_ENDPOINTURL:}")
    private String foreldrepengerEndpointUrl;
    @Value("${servicegateway.url:}")
    private String servicegatewayUrl;
    @Inject
    private StsConfig stsConfig;

    @Bean
    public ForeldrepengerV2 foreldrepengerPortType() {
        return MetricsFactory.createTimerProxyForWebService("Foreldrepenger_v2", getForeldrepengerPortType(false), ForeldrepengerV2.class);
    }

    @Bean
    public Pingable foreldrepenger() {
        return new PingableWebService("Foreldrepenger", getForeldrepengerPortType(true));
    }

    private ForeldrepengerV2 getForeldrepengerPortType(boolean isPingPorttype) {
        CXFClient<ForeldrepengerV2> cxfClient = new CXFClient<>(ForeldrepengerV2.class)
                .wsdl("classpath:wsdl/tjenestespesifikasjon/no/nav/tjeneste/virksomhet/foreldrepenger/v2/Binding.wsdl")
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/foreldrepenger/v2/Binding", "Foreldrepenger_v2"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/foreldrepenger/v2/Binding", "Foreldrepenger_v2Port"))
                .withHandler(new MDCOutHandler())
                .address(getAdress());

        if (isPingPorttype) {
            cxfClient.configureStsForSystemUser(stsConfig);
        } else {
            cxfClient.configureStsForSubject(stsConfig);
        }

        return cxfClient.build();
    }

    private String getAdress() {
        return defaultIfBlank(servicegatewayUrl, foreldrepengerEndpointUrl);
    }
}
