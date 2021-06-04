package no.nav.modiapersonoversikt.config.endpoint.util;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.virksomhet.aktoer.v2.Aktoer_v2;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.InnsynJournalV2;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class CacheTestConfig {
    @Bean
    public Aktoer_v2 aktoerPortType() {
        return mock(Aktoer_v2.class);
    }

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
    public KodeverkPortType kodeverkPortType() {
        return mock(KodeverkPortType.class);
    }

    @Bean
    public OrganisasjonEnhetV2 organisasjonEnhetV2() {
        return mock(OrganisasjonEnhetV2.class);
    }

    @Bean
    public OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1() {
        return mock(OrganisasjonEnhetKontaktinformasjonV1.class);
    }

    @Bean
    public OrganisasjonV4 organisasjonV4PortType() {
        return mock(OrganisasjonV4.class);
    }
}
