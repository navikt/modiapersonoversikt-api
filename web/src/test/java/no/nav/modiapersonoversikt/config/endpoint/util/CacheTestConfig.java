package no.nav.modiapersonoversikt.config.endpoint.util;

import no.nav.modiapersonoversikt.consumer.ldap.LDAPService;
import no.nav.modiapersonoversikt.consumer.ldap.LDAPServiceImpl;
import no.nav.modiapersonoversikt.consumer.norg.NorgApi;
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolging;
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolgingServiceImpl;
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService;
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class CacheTestConfig {

    @Bean
    public NorgApi norgApi() {
        return mock(NorgApi.class);
    }

    @Bean
    public LDAPService ldapService() {

        return mock(LDAPServiceImpl.class);
    }

    @Bean
    public ArbeidsrettetOppfolging.Service oppfolgingsApi() {

        return mock(ArbeidsrettetOppfolgingServiceImpl.class);
    }

    @Bean
    public PdlOppslagService pdlOppslagService() {
        System.setProperty("PDL_API_URL", "http://dummy.no");
        return mock(PdlOppslagServiceImpl.class);
    }
}
