package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.hentsaker;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakSakV1PortTypeMock.createGsakSakV1Mock;

@Configuration
public class GsakSakV1EndpointConfig {

    public static final String GSAK_SAK_KEY = "start.gsak.sak.withmock";

    @Bean
    public SakV1 sakEndpoint() {
        SakV1 prod = createEndpoint();
        SakV1 mock = createGsakSakV1Mock();

        return createMetricsProxyWithInstanceSwitcher("SakV1", prod, mock, GSAK_SAK_KEY, SakV1.class);
    }

    @Bean
    public Pingable gsakSakslistePing() {
        return new PingableWebService("Gsak - sak", createEndpoint());
    }

    private static SakV1 createEndpoint() {
        return new CXFClient<>(SakV1.class)
                .timeout(15000, 15000)
                .address(EnvironmentUtils.getRequiredProperty("gsak.sak.v1.url"))
                .configureStsForSystemUserInFSS()
                .build();
    }
}
