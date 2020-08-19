package no.nav.sykmeldingsperioder.config.spring.sykepenger;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.metrics.MetricsFactory;
import no.nav.modig.jaxws.handlers.MDCOutHandler;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.sykepenger.v2.SykepengerV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Configuration
public class SykepengerConsumerConfig {

    @Value("${VIRKSOMHET_SYKEPENGER_V2_ENDPOINTURL:}")
    private String sykepengerEndpointUrl;
    @Value("${servicegateway.url:}")
    private String servicegatewayUrl;

    @Inject
    private StsConfig stsConfig;

    @Bean
    public SykepengerV2 sykepengerPortType() {
        return MetricsFactory.createTimerProxyForWebService("Sykepenger_v2", getSykepengerPortType(false), SykepengerV2.class);
    }

    @Bean
    public Pingable sykepengerPingable() {
        return new PingableWebService("Sykepenger", getSykepengerPortType(true));
    }

    private SykepengerV2 getSykepengerPortType(boolean isPingPorttype) {
        CXFClient<SykepengerV2> cxfClient = new CXFClient<>(SykepengerV2.class)
                .wsdl("classpath:wsdl/tjenestespesifikasjon/no/nav/tjeneste/virksomhet/sykepenger/v2/Binding.wsdl")
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/sykepenger/v2/Binding", "Sykepenger_v2"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/sykepenger/v2/Binding", "Sykepenger_v2Port"))
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
        return defaultIfBlank(servicegatewayUrl, sykepengerEndpointUrl);
    }
}
