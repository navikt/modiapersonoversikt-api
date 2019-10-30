package no.nav.kontrakter.consumer.fim.config;

import no.nav.modig.jaxws.handlers.MDCOutHandler;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Configuration
public class YtelseskontraktConsumerConfig {

    @Value("${ytelseskontraktendpoint.url:}")
    private String ytelseskontraktEndpointUrl;
    @Value("${servicegateway.url:}")
    private String servicegatewayUrl;

    @Bean
    public YtelseskontraktV3 ytelseskontraktV3() {
        return getYtelseskontraktV3()
                .configureStsForSubject()
                .build();
    }


    @Bean
    public Pingable ytelseskontraktV3Pingable() {
        YtelseskontraktV3 pingPorttype = getYtelseskontraktV3().configureStsForSystemUser().build();
        return new PingableWebService("Ytelseskontrakt", pingPorttype);
    }

    private CXFClient<YtelseskontraktV3> getYtelseskontraktV3() {
        return new CXFClient<>(YtelseskontraktV3.class)
                .wsdl("classpath:ytelser/no/nav/tjeneste/virksomhet/ytelseskontrakt/v3/Binding.wsdl")
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/ytelseskontrakt/v3/Binding", "Ytelseskontrakt_v3"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/ytelseskontrakt/v3/Binding", "Ytelseskontrakt_v3Port"))
                .withHandler(new MDCOutHandler())
                .address(getAdress());
    }

    private String getAdress() {
        return defaultIfBlank(servicegatewayUrl, ytelseskontraktEndpointUrl);
    }
}
