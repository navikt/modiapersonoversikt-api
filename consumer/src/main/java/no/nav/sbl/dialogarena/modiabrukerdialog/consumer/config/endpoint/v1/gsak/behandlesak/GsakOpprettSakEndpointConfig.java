package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.behandlesak;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.behandlesak.v1.BehandleSakV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class GsakOpprettSakEndpointConfig {
    @Inject
    private StsConfig stsConfig;

    @Bean
    public BehandleSakV1 behandleSakV1() {
        BehandleSakV1 prod = createGsakOpprettSakPortType();

        return createTimerProxyForWebService("behandleSakV1", prod, BehandleSakV1.class);
    }

    @Bean
    public Pingable behandleSakPing() {
        return new PingableWebService("Gsak - opprett sak", createGsakOpprettSakPortType());
    }

    private BehandleSakV1 createGsakOpprettSakPortType() {
        return new CXFClient<>(BehandleSakV1.class)
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_BEHANDLESAK_V1_ENDPOINTURL"))
                .configureStsForSystemUser(stsConfig)
                .build();
    }
}
