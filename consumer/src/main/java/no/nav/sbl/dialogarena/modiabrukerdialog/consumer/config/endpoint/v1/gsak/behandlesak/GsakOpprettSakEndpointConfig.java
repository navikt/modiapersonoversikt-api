package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.behandlesak;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.behandlesak.v1.BehandleSakV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.hentsaker.GsakSakV1EndpointConfig.GSAK_SAK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TimingMetricsProxy.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakOpprettSakEndpointMock.createGsakOpprettSakPortTypeMock;

@Configuration
public class GsakOpprettSakEndpointConfig {
    @Bean
    public BehandleSakV1 behandleSakV1() {
        BehandleSakV1 prod = createGsakOpprettSakPortType();
        BehandleSakV1 mock = createGsakOpprettSakPortTypeMock();

        return createMetricsProxyWithInstanceSwitcher(prod, mock, GSAK_SAK_KEY, BehandleSakV1.class);
    }

    @Bean
    public Pingable behandleSakPing(final BehandleSakV1 ws) {
        return new PingableWebService("GSAK_BEHANDLESAK_V1", ws);
    }

    private static BehandleSakV1 createGsakOpprettSakPortType() {
        return new CXFClient<>(BehandleSakV1.class)
                .address(System.getProperty("gsak.behandlesak.v1.url"))
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .build();
    }
}
