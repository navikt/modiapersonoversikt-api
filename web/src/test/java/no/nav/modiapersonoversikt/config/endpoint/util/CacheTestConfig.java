package no.nav.modiapersonoversikt.config.endpoint.util;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import no.nav.modiapersonoversikt.config.endpoint.kodeverksmapper.Kodeverksmapper;
import no.nav.modiapersonoversikt.consumer.norg.NorgApi;
import no.nav.modiapersonoversikt.legacy.api.service.ldap.LDAPService;
import no.nav.modiapersonoversikt.legacy.api.service.oppfolgingsinfo.OppfolgingsinfoApiService;
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService;
import no.nav.modiapersonoversikt.service.ScheduledAnsattListePrefetch;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.InnsynJournalV2;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class CacheTestConfig {
    @Bean
    public HenvendelseSoknaderPortType henvendelseSoknaderPortType() {
        return mock(HenvendelseSoknaderPortType.class);
    }

    @Bean
    public InnsynJournalV2 innsynJournalV2() {
        return mock(InnsynJournalV2.class);
    }

    @Bean
    public GOSYSNAVOrgEnhet gosysNavOrgEnhet() {
        return mock(GOSYSNAVOrgEnhet.class);
    }

    @Bean
    public GOSYSNAVansatt gosysNavAnsatt() {
        return mock(GOSYSNAVansatt.class);
    }

    @Bean
    public OppfoelgingPortType oppfolgingPortType() {
        return mock(OppfoelgingPortType.class);
    }

    @Bean
    public SakOgBehandlingV1 sakOgBehandlingPortType() {
        return mock(SakOgBehandlingV1.class);
    }

    @Bean
    public HenvendelsePortType henvendelsePortType() {
        return mock(HenvendelsePortType.class);
    }

    @Bean
    public NorgApi norgApi() {
        return mock(NorgApi.class);
    }

    @Bean
    public OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1() {
        return mock(OrganisasjonEnhetKontaktinformasjonV1.class);
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
    public Kodeverksmapper kodeverksmapper() {
        return mock(Kodeverksmapper.class);
    }

    @Bean
    public PdlOppslagService pdlOppslagService() { return mock(PdlOppslagService.class); }

    @Bean
    public ScheduledAnsattListePrefetch scheduledAnsattListePrefetch() {
        return new ScheduledAnsattListePrefetch();
    }
}
