package no.nav.sbl.dialogarena.sporsmalogsvar.config.mock;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.*;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.binding.BehandleJournalV2;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;
import no.nav.virksomhet.tjenester.sak.v1.Sak;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMockSaker;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.opprettMeldingEksempel;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class ServiceTestContext {

    @Bean
    public GsakKodeverk gsakKodeverk() {
        return mock(GsakKodeverk.class);
    }

    @Bean
    public LokaltKodeverk lokaltKodeverk() {
        return mock(LokaltKodeverk.class);
    }

    @Bean
    public StandardKodeverk standardKodeverk() {
        return mock(StandardKodeverk.class);
    }

    @Bean(name = "kodeverkPortTypeV2")
    public KodeverkPortType kodeverkPortType() {
        return mock(KodeverkPortType.class);
    }

    @Bean
    public GsakService gsakService() {
        return mock(GsakService.class);
    }

    @Bean
    public SakerService sakerService() {
        SakerService sakerService = mock(SakerService.class);
        when(sakerService.hentSaker(anyString())).thenReturn(createMockSaker());
        return sakerService;
    }

    @Bean
    public HenvendelseBehandlingService henvendelseBehandlingService() {
        HenvendelseBehandlingService henvendelseBehandlingService = mock(HenvendelseBehandlingService.class);
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(opprettMeldingEksempel()));
        return henvendelseBehandlingService;
    }

    @Bean
    public HenvendelsePortType henvendelsePortType() {
        return mock(HenvendelsePortType.class);
    }

    @Bean
    public BehandleHenvendelsePortType behandleHenvendelsePortType() {
        return mock(BehandleHenvendelsePortType.class);
    }

    @Bean
    public BehandleJournalV2 behandleJournalV2() {
        return mock(BehandleJournalV2.class);
    }

    @Bean
    public OppgavebehandlingV3 oppgavebehandling() {
        return mock(OppgavebehandlingV3.class);
    }

    @Bean
    public OppgaveV3 oppgaveV3() {
        return mock(OppgaveV3.class);
    }

    @Bean
    public Sak sakWs() {
        return mock(Sak.class);
    }

    @Bean
    public AnsattService ansattService() {
        return mock(AnsattService.class);
    }

    @Bean
    public GOSYSNAVansatt GOSYSNAVansatt() {
        return mock(GOSYSNAVansatt.class);
    }

    @Bean
    public GOSYSNAVOrgEnhet GOSYSNAVOrgEnhet() {
        return mock(GOSYSNAVOrgEnhet.class);
    }

    @Bean
    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        return mock(PersonKjerneinfoServiceBi.class);
    }

    @Bean
    public SaksbehandlerInnstillingerService saksbehandlerInnstillingerService() {
        return mock(SaksbehandlerInnstillingerService.class);
    }

    @Bean
    public EnhetService enhetService() {
        EnhetService service = mock(EnhetService.class);
        when(service.hentAlleEnheter()).thenReturn(asList(new AnsattEnhet("1231", "Sinsen")));
        return service;
    }

    @Bean
    public Ruting ruting() {
        return mock(Ruting.class);
    }

    @Bean(name = "pep")
    public EnforcementPoint enforcementPoint() {
        return mock(EnforcementPoint.class);
    }

}
