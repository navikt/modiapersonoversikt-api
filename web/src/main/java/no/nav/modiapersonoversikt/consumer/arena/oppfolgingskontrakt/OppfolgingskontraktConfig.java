package no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.modiapersonoversikt.infrastructure.jaxws.handlers.MDCOutHandler;
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;

@Configuration
@EnableCaching
public class OppfolgingskontraktConfig {
    private final String oppfolgingskontraktEndpointUrl = getRequiredProperty("VIRKSOMHET_OPPFOLGING_V1_ENDPOINTURL");

    @Bean
    public OppfolgingskontraktService oppfolgingskontraktService(StsConfig stsConfig) {
        OppfoelgingPortType soapService = getOppfolgingPortType()
                .configureStsForSubject(stsConfig)
                .build();

        OppfolgingskontraktServiceImpl service = new OppfolgingskontraktServiceImpl();
        service.setOppfolgingskontraktService(soapService);
        service.setMapper(OppfolgingskontraktMapper.getInstance());

        return service;
    }

    @Bean
    public Pingable oppfoelgingPingable(StsConfig stsConfig) {
        OppfoelgingPortType pingPorttype = getOppfolgingPortType()
                .configureStsForSystemUser(stsConfig)
                .build();
        return new PingableWebService("Oppfoelging", pingPorttype);
    }

    private CXFClient<OppfoelgingPortType> getOppfolgingPortType() {
        return new CXFClient<>(OppfoelgingPortType.class)
                .address(oppfolgingskontraktEndpointUrl)
                .withHandler(new MDCOutHandler());
    }
}
