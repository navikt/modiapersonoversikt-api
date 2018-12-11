package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.oppfolgingsinfo;

import no.nav.modig.modia.ping.FailedPingResult;
import no.nav.modig.modia.ping.OkPingResult;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsinfoApiService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo.OppfolgingsinfoApiServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo.OppfolgingsinfoApiServiceMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class OppfolgingsinfoApiEndpointConfig {
    private static final String MOCK_KEY = "veilarboppfolging.api.withmock";
    private String api = System.getProperty("veilarboppfolging.api.url");

    @Bean
    public OppfolgingsinfoApiService lagOppfolgingsApi() {
        return createMetricsProxyWithInstanceSwitcher(
                "veilarboppfolgingApi",
                new OppfolgingsinfoApiServiceImpl(api),
                new OppfolgingsinfoApiServiceMock(),
                MOCK_KEY,
                OppfolgingsinfoApiService.class
        );
    }

    @Bean
    public Pingable Ping() {
        return new Pingable() {
            @Override
            public PingResult ping() {
                OppfolgingsinfoApiService oppfolgingsApi= lagOppfolgingsApi();
                Timer timer = Timer.lagOgStartTimer();
                try {
                    oppfolgingsApi.ping();
                    return new OkPingResult(timer.stoppOgHentTid());
                } catch (IOException e) {
                    return new FailedPingResult(e, timer.stoppOgHentTid());
                }
            }

            @Override
            public String name() {
                return "OppfolgingsInfoApi";
            }

            @Override
            public String method() {
                return "ping";
            }

            @Override
            public String endpoint() {
                return getProperty("veilarboppfolging.api.url");
            }
        };
    }


}
