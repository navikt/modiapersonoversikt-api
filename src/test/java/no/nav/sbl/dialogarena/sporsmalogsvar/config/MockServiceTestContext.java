package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modig.common.SporingsLogger;
import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.content.PropertyResolver;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Traader;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.virksomhet.tjenester.sak.v1.Sak;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.opprettMeldingEksempel;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class MockServiceTestContext {

    @Bean
    public GsakKodeverk gsakKodeverk() {
        return mock(GsakKodeverk.class);
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
    public ArbeidsfordelingV1Service arbeidsfordelingV1Service() {
        return mock(ArbeidsfordelingV1Service.class);
    }

    @Bean
    @Named("henvendelseBehandlingServiceMock")
    public HenvendelseBehandlingService henvendelseBehandlingService() {
        HenvendelseBehandlingService henvendelseBehandlingService = mock(HenvendelseBehandlingService.class);
        when(henvendelseBehandlingService.getEnhet(anyString())).thenReturn("1234");
        when(henvendelseBehandlingService.hentTraader(anyString(), anyString())).thenReturn(new Traader(asList(opprettMeldingEksempel())));
        return henvendelseBehandlingService;
    }

    @Bean
    public OppgaveBehandlingService oppgaveBehandlingService() {
        return mock(OppgaveBehandlingService.class);
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
    public OrganisasjonEnhetV2Service organisasjonEnhetV2Service() {
        OrganisasjonEnhetV2Service organisasjonEnhetService = mock(OrganisasjonEnhetV2Service.class);
        when(organisasjonEnhetService.hentAlleEnheter(OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.KUN_OPPGAVEBEHANDLERE)).thenReturn(asList(new AnsattEnhet("1231", "Sinsen", "AKTIV")));
        return organisasjonEnhetService;
    }

    @Bean(name = "pep")
    public EnforcementPoint enforcementPoint() {
        return mock(EnforcementPoint.class);
    }

    @Bean
    public CmsContentRetriever contentRetriever() {
        return mock(CmsContentRetriever.class);
    }

    @Bean
    public PropertyResolver propertyResolver() {
        return mock(PropertyResolver.class);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public SporingsLogger sporingsLogger() {
        return mock(SporingsLogger.class);
    }

    @Bean
    public LDAPService ldapService() {
        return mock(LDAPService.class);
    }

}
