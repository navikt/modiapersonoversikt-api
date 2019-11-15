package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.oppfolgingsinfo;

import no.nav.modig.modia.ping.FailedPingResult;
import no.nav.modig.modia.ping.OkPingResult;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsinfoApiService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo.OppfolgingsinfoApiServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Timer;
import no.nav.sbl.util.EnvironmentUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class OppfolgingsinfoApiEndpointConfig {
    private String api = EnvironmentUtils.getRequiredProperty("VEILARBOPPFOLGINGAPI_URL");

    @Bean
    public OppfolgingsinfoApiService lagOppfolgingsApi() {
        return createTimerProxyForWebService(
                "veilarboppfolgingApi",
                new OppfolgingsinfoApiServiceImpl(api),
                OppfolgingsinfoApiService.class
        );
    }

    @Bean
    public Pingable Ping() {
        return new Pingable() {
            @Override
            public PingResult ping() {
                OppfolgingsinfoApiService oppfolgingsApi = lagOppfolgingsApi();
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
                return EnvironmentUtils.getRequiredProperty("VEILARBOPPFOLGINGAPI_URL");
            }
        };
    }
}
