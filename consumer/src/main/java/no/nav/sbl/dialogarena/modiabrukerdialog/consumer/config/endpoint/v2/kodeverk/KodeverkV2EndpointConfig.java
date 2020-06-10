package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.kodeverk;

import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.common.kodeverk.CachingKodeverkClient;
import no.nav.sbl.dialogarena.common.kodeverk.DefaultKodeverkClient;
import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Optional.empty;
import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;
import static org.apache.cxf.ws.security.SecurityConstants.MUST_UNDERSTAND;

@Configuration
public class KodeverkV2EndpointConfig {

    @Bean(name = "kodeverkPortTypeV2")
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
