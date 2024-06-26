package no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.modiapersonoversiktproxy.infrastructure.handlers.MDCOutHandler;
import no.nav.modiapersonoversiktproxy.infrastructure.ping.Pingable;
import no.nav.modiapersonoversiktproxy.infrastructure.ping.PingableWebService;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;

@Configuration
public class YtelseskontraktConfig {
    private final String ytelseskontraktEndpointUrl = getRequiredProperty("VIRKSOMHET_YTELSESKONTRAKT_V3_ENDPOINTURL");

    @Bean
    public YtelseskontraktService ytelseskontraktService(StsConfig stsConfig) {
        YtelseskontraktV3 soapService = getYtelseskontraktV3()
                .configureStsForSubject(stsConfig)
                .build();

        YtelseskontraktServiceImpl service = new YtelseskontraktServiceImpl();
        service.setYtelseskontraktService(soapService);
        service.setMapper(YtelseskontraktMapper.getInstance());

        return service;
    }

    @Bean
    public Pingable ytelseskontraktV3Pingable(StsConfig stsConfig) {
        YtelseskontraktV3 pingPorttype = getYtelseskontraktV3().configureStsForSystemUser(stsConfig).build();
        return new PingableWebService("Ytelseskontrakt", pingPorttype);
    }

    private CXFClient<YtelseskontraktV3> getYtelseskontraktV3() {
        return new CXFClient<>(YtelseskontraktV3.class)
                .wsdl("classpath:wsdl/tjenestespesifikasjon/no/nav/tjeneste/virksomhet/ytelseskontrakt/v3/Binding.wsdl")
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/ytelseskontrakt/v3/Binding", "Ytelseskontrakt_v3"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/ytelseskontrakt/v3/Binding", "Ytelseskontrakt_v3Port"))
                .withHandler(new MDCOutHandler())
                .address(ytelseskontraktEndpointUrl);
    }
}
