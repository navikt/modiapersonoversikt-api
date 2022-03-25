package no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.modiapersonoversikt.infrastructure.jaxws.handlers.MDCOutHandler;
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Configuration
public class OppfolgingskontraktConfig {

    @Value("${VIRKSOMHET_OPPFOLGING_V1_ENDPOINTURL:}")
    private String oppfolgingskontraktEndpointUrl;
    @Value("${servicegateway.url:}")
    private String servicegatewayUrl;

    @Autowired
    StsConfig stsConfig;

    @Bean
    public OppfolgingskontraktService oppfolgingskontraktService() {
        OppfoelgingPortType soapService = getOppfolgingPortType()
                .configureStsForSubject(stsConfig)
                .build();

        OppfolgingskontraktServiceImpl service = new OppfolgingskontraktServiceImpl();
        service.setOppfolgingskontraktService(soapService);
        service.setMapper(OppfolgingskontraktMapper.getInstance());

        return service;
    }

    @Bean
    public Pingable oppfoelgingPingable() {
        OppfoelgingPortType pingPorttype = getOppfolgingPortType()
                .configureStsForSystemUser(stsConfig)
                .build();
        return new PingableWebService("Oppfoelging", pingPorttype);
    }

    private CXFClient<OppfoelgingPortType> getOppfolgingPortType() {
        return new CXFClient<>(OppfoelgingPortType.class)
                .address(getAdress())
                .withHandler(new MDCOutHandler());
    }

    private String getAdress() {
        return defaultIfBlank(servicegatewayUrl, oppfolgingskontraktEndpointUrl);
    }
}
