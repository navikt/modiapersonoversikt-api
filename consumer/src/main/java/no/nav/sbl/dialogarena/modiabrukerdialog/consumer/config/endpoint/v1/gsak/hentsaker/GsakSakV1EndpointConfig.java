package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.hentsaker;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.metrics.TimingMetricsProxy.createMetricsProxy;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakSakV1PortTypeMock.createGsakSakV1Mock;

@Configuration
public class GsakSakV1EndpointConfig {

    public static final String GSAK_SAK_KEY = "start.gsak.sak.withmock";

    @Bean
    public SakV1 sakEndpoint() {
        SakV1 prod = createMetricsProxy(createEndpoint(), SakV1.class);
        SakV1 mock = createGsakSakV1Mock();

        return createSwitcher(prod, mock, GSAK_SAK_KEY, SakV1.class);
    }

    @Bean
    public Pingable gsakSakslistePing(final SakV1 ws) {
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                long start = System.currentTimeMillis();
                String name = "GSAK_SAK_V1";
                try {
                    ws.ping();
                    return asList(new PingResult(name, SERVICE_OK, System.currentTimeMillis() - start));
                } catch (Exception e) {
                    return asList(new PingResult(name, SERVICE_FAIL, System.currentTimeMillis() - start));
                }
            }
        };
    }

    private static SakV1 createEndpoint() {
        return new CXFClient<>(SakV1.class)
                .address(System.getProperty("gsak.sak.v1.url"))
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .build();
    }
}
