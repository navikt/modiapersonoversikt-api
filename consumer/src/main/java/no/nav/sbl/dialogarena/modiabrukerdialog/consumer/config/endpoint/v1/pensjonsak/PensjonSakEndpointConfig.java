package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.pensjonsak;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.PensjonSakEndpointMock.createPensjonSakV1Mock;

@Configuration
public class PensjonSakEndpointConfig {

    public static final String PENSJONSAK_KEY = "pensjon.sak.withmock";

    @Bean
    public PensjonSakV1 pensjonSakV1() {
        PensjonSakV1 prod = createPensjonSakV1().configureStsForOnBehalfOfWithJWT().build();
        PensjonSakV1 mock = createPensjonSakV1Mock();
        return createMetricsProxyWithInstanceSwitcher("PensjonSakV1", prod, mock, PENSJONSAK_KEY, PensjonSakV1.class);
    }

    @Bean
    public Pingable pensjonSakV1Ping() {
        final PensjonSakV1 ws = createPensjonSakV1().configureStsForSystemUserInFSS().build();
        return new PingableWebService("Pesys - Pensjonsak", ws);
    }

    private static CXFClient<PensjonSakV1> createPensjonSakV1() {
        return new CXFClient<>(PensjonSakV1.class)
                .timeout(15000, 15000)
                .address(System.getProperty("pensjon.sak.v1.url"));
    }
}
