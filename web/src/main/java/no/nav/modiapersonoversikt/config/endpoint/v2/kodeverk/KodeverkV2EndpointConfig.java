package no.nav.modiapersonoversikt.config.endpoint.v2.kodeverk;

import no.nav.common.cxf.CXFClient;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService;
import no.nav.modiapersonoversikt.integration.kodeverk2.CachingKodeverkClient;
import no.nav.modiapersonoversikt.integration.kodeverk2.DefaultKodeverkClient;
import no.nav.modiapersonoversikt.integration.kodeverk2.KodeverkClient;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Optional.empty;
import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;
import static org.apache.cxf.ws.security.SecurityConstants.MUST_UNDERSTAND;

@Configuration
public class KodeverkV2EndpointConfig {

    @Bean
    public KodeverkPortType kodeverkPortType() {
        KodeverkPortType prod = lagKodeverkPortType();

        return createTimerProxyForWebService("KodeverkPortTypeV2", prod, KodeverkPortType.class);
    }

    @Bean
    public KodeverkClient kodeverkClient(KodeverkPortType kodeverkPortType) {
        return new CachingKodeverkClient(new DefaultKodeverkClient(kodeverkPortType), empty());
    }

    @Bean
    public Pingable pingKodeverk() {
        return new PingableWebService("Kodeverk", lagKodeverkPortType());
    }

    private KodeverkPortType lagKodeverkPortType() {
        return new CXFClient<>(KodeverkPortType.class)
                .timeout(15000, 15000)
                .wsdl("classpath:wsdl/no/nav/tjeneste/virksomhet/kodeverk/v2/Kodeverk.wsdl")
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_KODEVERK_V2_ENDPOINTURL"))
                .withProperty(MUST_UNDERSTAND, false)
                .build();
    }
}
