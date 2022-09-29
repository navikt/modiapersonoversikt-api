package no.nav.modiapersonoversikt.consumer.veilarboppfolging;

import no.nav.common.token_client.client.OnBehalfOfTokenClient;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.consumer.ldap.LDAPService;
import no.nav.modiapersonoversikt.infrastructure.ping.ConsumerPingable;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.modiapersonoversikt.utils.DownstreamApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.modiapersonoversikt.utils.TokenUtilsKt.bindTo;

@Configuration
public class ArbeidsrettetOppfolgingConfig {
    private final String api = EnvironmentUtils.getRequiredProperty("VEILARBOPPFOLGINGAPI_URL");
    private final DownstreamApi scope = DownstreamApi.parse(EnvironmentUtils.getRequiredProperty("VEILARBOPPFOLGINGAPI_SCOPE"));

    @Bean
    public ArbeidsrettetOppfolging.Service oppfolgingsApi(
            LDAPService ldapService,
            OnBehalfOfTokenClient onBehalfOfTokenClient
    ) {
        return new ArbeidsrettetOppfolgingServiceImpl(api, ldapService, bindTo(onBehalfOfTokenClient, scope));
    }

    @Bean
    public Pingable oppfolgingsApiPing(ArbeidsrettetOppfolging.Service endepunkt) {
        return new ConsumerPingable(
                "OppfolgingsInfoApi",
                endepunkt::ping
        );
    }
}
