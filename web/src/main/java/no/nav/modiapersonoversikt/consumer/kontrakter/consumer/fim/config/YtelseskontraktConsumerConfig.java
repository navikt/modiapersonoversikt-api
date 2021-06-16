package no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.config;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.modiapersonoversikt.infrastructure.jaxws.handlers.MDCOutHandler;
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import javax.xml.namespace.QName;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Configuration
public class YtelseskontraktConsumerConfig {

    @Value("${VIRKSOMHET_YTELSESKONTRAKT_V3_ENDPOINTURL:}")
    private String ytelseskontraktEndpointUrl;
    @Value("${servicegateway.url:}")
    private String servicegatewayUrl;
    @Autowired
    private StsConfig stsConfig;

    @Bean
    public YtelseskontraktV3 ytelseskontraktV3() {
        return getYtelseskontraktV3()
                .configureStsForSubject(stsConfig)
                .build();
    }


    @Bean
    public Pingable ytelseskontraktV3Pingable() {
        YtelseskontraktV3 pingPorttype = getYtelseskontraktV3().configureStsForSystemUser(stsConfig).build();
        return new PingableWebService("Ytelseskontrakt", pingPorttype);
    }

    private CXFClient<YtelseskontraktV3> getYtelseskontraktV3() {
        return new CXFClient<>(YtelseskontraktV3.class)
                .wsdl("classpath:wsdl/tjenestespesifikasjon/no/nav/tjeneste/virksomhet/ytelseskontrakt/v3/Binding.wsdl")
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/ytelseskontrakt/v3/Binding", "Ytelseskontrakt_v3"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/ytelseskontrakt/v3/Binding", "Ytelseskontrakt_v3Port"))
                .withHandler(new MDCOutHandler())
                .address(getAdress());
    }

    private String getAdress() {
        return defaultIfBlank(servicegatewayUrl, ytelseskontraktEndpointUrl);
    }
}
