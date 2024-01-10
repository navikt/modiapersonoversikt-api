package no.nav.modiapersonoversikt.config.endpoint.util;

import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.OppfolgingskontraktService;
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.OppfolgingskontraktServiceImpl;
import no.nav.modiapersonoversikt.consumer.ldap.LDAPService;
import no.nav.modiapersonoversikt.consumer.ldap.LDAPServiceImpl;
import no.nav.modiapersonoversikt.consumer.norg.NorgApi;
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolging;
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolgingServiceImpl;
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService;
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagServiceImpl;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class CacheTestConfig {

    @Bean
    public OppfolgingskontraktService oppfolgingskontraktService() {
        return mock(OppfolgingskontraktServiceImpl.class);
    }

    @Bean
    public SakOgBehandlingV1 sakOgBehandlingPortType() {
        return mock(SakOgBehandlingV1.class);
    }

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
