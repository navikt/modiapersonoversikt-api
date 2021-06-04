package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.aktor;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.modig.jaxws.handlers.MDCOutHandler;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.aktoer.v2.Aktoer_v2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;


@Configuration
public class AktorV2EndpointConfig {

    @Value("${AKTOER_V2_ENDPOINTURL}")
    private String aktoerUrl;

    @Autowired
    private StsConfig stsConfig;

    private Aktoer_v2 aktoerPort() {
        return new CXFClient<>(Aktoer_v2.class)
                .timeout(15000, 15000)
                .address(aktoerUrl)
                .configureStsForSystemUser(stsConfig)
                .withHandler(new MDCOutHandler())
                .build();
    }

    @Bean
    public Aktoer_v2 aktoerPortType() {
        final Aktoer_v2 prod = aktoerPort();

        return createTimerProxyForWebService("AktoerV2", prod, Aktoer_v2.class);
    }


    @Bean
    public Pingable pingAktoer() {
        return new PingableWebService("AktorV2", aktoerPort());
    }
}
