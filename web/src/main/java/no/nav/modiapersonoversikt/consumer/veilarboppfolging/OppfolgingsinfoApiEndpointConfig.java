package no.nav.modiapersonoversikt.consumer.veilarboppfolging;

import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.infrastructure.ping.ConsumerPingable;
import no.nav.modiapersonoversikt.legacy.api.service.oppfolgingsinfo.OppfolgingsinfoApiService;
import no.nav.modiapersonoversikt.service.oppfolgingsinfo.OppfolgingsinfoApiServiceImpl;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
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
