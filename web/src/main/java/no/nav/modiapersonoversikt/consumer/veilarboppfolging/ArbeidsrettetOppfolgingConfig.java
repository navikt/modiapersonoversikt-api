package no.nav.modiapersonoversikt.consumer.veilarboppfolging;

import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.consumer.ldap.LDAPService;
import no.nav.modiapersonoversikt.infrastructure.ping.ConsumerPingable;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArbeidsrettetOppfolgingConfig {
    private final String api = EnvironmentUtils.getRequiredProperty("VEILARBOPPFOLGINGAPI_URL");

    @Bean
    public ArbeidsrettetOppfolging.Service oppfolgingsApi(
            LDAPService ldapService
    ) {
        return new ArbeidsrettetOppfolgingServiceImpl(api, ldapService);
    }

    @Bean
    public Pingable oppfolgingsApiPing(ArbeidsrettetOppfolging.Service endepunkt) {
        return new ConsumerPingable(
                "OppfolgingsInfoApi",
                endepunkt::ping
        );
    }
}
