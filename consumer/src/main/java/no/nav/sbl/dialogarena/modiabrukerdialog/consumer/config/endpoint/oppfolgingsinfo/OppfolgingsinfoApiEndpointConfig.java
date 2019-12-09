package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.oppfolgingsinfo;

import no.nav.modig.modia.ping.ConsumerPingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsinfoApiService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo.OppfolgingsinfoApiServiceImpl;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.util.EnvironmentUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OppfolgingsinfoApiEndpointConfig {
    private String api = EnvironmentUtils.getRequiredProperty("VEILARBOPPFOLGINGAPI_URL");

    @Bean
    public OppfolgingsinfoApiService oppfolgingsApi() {
        return new OppfolgingsinfoApiServiceImpl(api);
    }

    @Bean
    public Pingable oppfolgingsApiPing() {
        OppfolgingsinfoApiService endepunkt = oppfolgingsApi();
        return new ConsumerPingable(
                "OppfolgingsInfoApi",
                endepunkt::ping
        );
    }
}
