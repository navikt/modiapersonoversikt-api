package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.hentsaker;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class GsakSakV1EndpointConfig {
    @Inject
    private StsConfig stsConfig;

    @Bean
    public SakV1 sakEndpoint() {
        SakV1 prod = createEndpoint();

        return createTimerProxyForWebService("SakV1", prod, SakV1.class);
    }

    @Bean
    public Pingable gsakSakslistePing() {
        return new PingableWebService("Gsak - sak", createEndpoint());
    }

    private SakV1 createEndpoint() {
        return new CXFClient<>(SakV1.class)
                .timeout(15000, 15000)
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_SAK_V1_ENDPOINTURL"))
                .configureStsForSystemUser(stsConfig)
                .build();
    }
}
