package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.hentsaker;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakSakV1PortTypeMock.createGsakSakV1Mock;

@Configuration
public class GsakSakV1EndpointConfig {

    public static final String GSAK_SAK_KEY = "start.gsak.sak.withmock";

    @Bean
    public SakV1 sakEndpoint() {
        SakV1 prod = createEndpoint();
        SakV1 mock = createGsakSakV1Mock();

        return createSwitcher(prod, mock, GSAK_SAK_KEY, SakV1.class);
    }

    @Bean
    public Pingable gsakSakslistePing() {
        return new PingableWebService("Gsak - sak", createEndpoint());
    }

    private static SakV1 createEndpoint() {
        return new CXFClient<>(SakV1.class)
                .address(System.getProperty("gsak.sak.v1.url"))
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .build();
    }
}
