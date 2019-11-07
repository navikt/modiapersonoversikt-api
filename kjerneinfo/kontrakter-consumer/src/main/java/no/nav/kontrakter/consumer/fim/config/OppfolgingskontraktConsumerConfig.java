package no.nav.kontrakter.consumer.fim.config;

import no.nav.modig.jaxws.handlers.MDCOutHandler;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Configuration
public class OppfolgingskontraktConsumerConfig {

    @Value("${VIRKSOMHET_OPPFOLGING_V1_ENDPOINTURL:}")
    private String oppfolgingskontraktEndpointUrl;
    @Value("${servicegateway.url:}")
    private String servicegatewayUrl;

    @Bean
    public OppfoelgingPortType oppfolgingPortType() {
        return createTimerProxyForWebService(
                "OppfoelgingV1",
                getOppfolgingPortType().configureStsForSubject().build(),
                OppfoelgingPortType.class
        );
    }

    @Bean
    public Pingable oppfoelgingPingable() {
        OppfoelgingPortType pingPorttype = getOppfolgingPortType()
                .configureStsForSystemUser()
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
