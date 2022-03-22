package no.nav.modiapersonoversikt.config.endpoint.util;

import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.OppfolgingskontraktService;
import no.nav.modiapersonoversikt.consumer.ldap.LDAPService;
import no.nav.modiapersonoversikt.consumer.norg.NorgApi;
import no.nav.modiapersonoversikt.legacy.api.service.oppfolgingsinfo.OppfolgingsinfoApiService;
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.InnsynJournalV2;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class CacheTestConfig {
    @Bean
    public InnsynJournalV2 innsynJournalV2() {
        return mock(InnsynJournalV2.class);
    }

    @Bean
    public OppfolgingskontraktService oppfolgingskontraktService() {
        return mock(OppfolgingskontraktService.class);
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
    public OrganisasjonV4 organisasjonV4PortType() {
        return mock(OrganisasjonV4.class);
    }

    @Bean
    public LDAPService ldapService() {
        return mock(LDAPService.class);
    }

    @Bean
    public OppfolgingsinfoApiService oppfolgingsApi() {
        return mock(OppfolgingsinfoApiService.class);
    }

    @Bean
    public PdlOppslagService pdlOppslagService() { return mock(PdlOppslagService.class); }
}
