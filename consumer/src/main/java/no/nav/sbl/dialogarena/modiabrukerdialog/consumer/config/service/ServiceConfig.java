package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modig.content.PropertyResolver;
import no.nav.modig.wicket.services.HealthCheckService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsenhetService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsinfoService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.PsakServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.oppgavebehandling.OppgaveBehandlingServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.DelsvarService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.DelsvarServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk.GsakKodeverkFraFil;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk.StandardKodeverkImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ldap.LDAPServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo.OppfolgingsenhetServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo.OppfolgingsinfoServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.OrganisasjonEnhetV2ServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerServiceImpl;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.OppfolgingsinfoV1;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.TildelOppgaveV1;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.inject.Named;

/**
 * MODIA ønsker å selv wire inn sine komponenters kontekster for å ha full kontroll over springoppsettet.
 */
@Configuration
@EnableScheduling
public class ServiceConfig {

    @Bean
    public LDAPService ldapService() {
        return new LDAPServiceImpl();
    }

    @Bean
    public HenvendelseUtsendingService henvendelseUtsendingService(HenvendelsePortType henvendelsePortType,
                                                                   SendUtHenvendelsePortType sendUtHenvendelsePortType,
                                                                   BehandleHenvendelsePortType behandleHenvendelsePortType,
                                                                   OppgaveBehandlingService oppgaveBehandlingService,
                                                                   SakerService sakerService,
                                                                   @Named("pep") EnforcementPoint pep,
                                                                   PropertyResolver propertyResolver,
                                                                   PersonKjerneinfoServiceBi personKjerneinfoServiceBi,
                                                                   LDAPService ldapService) {

        return new HenvendelseUtsendingServiceImpl(henvendelsePortType, sendUtHenvendelsePortType,
                behandleHenvendelsePortType, oppgaveBehandlingService, sakerService, pep,
                propertyResolver, personKjerneinfoServiceBi, ldapService);
    }

    @Bean
    public DelsvarService HenvendelseService(HenvendelseUtsendingService henvendelseUtsendingService) {
        return new DelsvarServiceImpl(henvendelseUtsendingService);
    }

    @Bean
    public OppgaveBehandlingService oppgaveBehandlingService(OppgavebehandlingV3 oppgavebehandlingV3, TildelOppgaveV1 tildelOppgaveV1,OppgaveV3 oppgaveV3,

                                                             AnsattService ansattService, Ruting ruting) {
        return new OppgaveBehandlingServiceImpl(oppgavebehandlingV3, tildelOppgaveV1, oppgaveV3, ansattService, ruting);
    }

    @Bean
    public AnsattService ansattService(GOSYSNAVansatt gosysNavAnsatt) {
        return new AnsattServiceImpl(gosysNavAnsatt);
    }

    @Bean
    public OrganisasjonEnhetV2Service organisasjonEnhetServiceV2() {
        return new OrganisasjonEnhetV2ServiceImpl();
    }

    @Bean
    public SaksbehandlerInnstillingerService saksbehandlerInnstillingerService(AnsattService ansattService) {
        return new SaksbehandlerInnstillingerServiceImpl(ansattService);
    }

    @Bean
    public HealthCheckService healthCheckService() {
        return new HealthCheckService();
    }

    @Bean
    public StandardKodeverk standardKodeverk() {
        return new StandardKodeverkImpl();
    }

    @Bean
    public GsakKodeverk gsakKodeverk() {
        return new GsakKodeverkFraFil();
    }

    @Bean
    public SakerService sakerService() {
        return new SakerServiceImpl();
    }

    @Bean
    public PsakService psakService(PensjonSakV1 pensjonSakV1) {
        return new PsakServiceImpl(pensjonSakV1);
    }

    @Bean
    public ScheduledAnsattListePrefetch scheduledAnsattListePrefetch() {
        return new ScheduledAnsattListePrefetch();
    }

    @Bean
    public GrunninfoService grunninfoService() {
        return new GrunninfoServiceImpl();
    }

    @Bean
    public OrganisasjonEnhetKontaktinformasjonService organisasjonEnhetKontaktinformasjon(OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1) {
        return new OrganisasjonEnhetKontaktinformasjonServiceImpl(organisasjonEnhetKontaktinformasjonV1);
    }

    @Bean
    public OppfolgingsinfoService oppfolgingsinfoService(OppfolgingsinfoV1 oppfolgingsinfoV1, LDAPService ldapService,
                                                         AktoerPortType aktoerPortType, OppfolgingsenhetService oppfolgingsenhetService) {
        return new OppfolgingsinfoServiceImpl(oppfolgingsinfoV1, ldapService, aktoerPortType, oppfolgingsenhetService);
    }

    @Bean
    public OppfolgingsenhetService oppfolgingsenhetService(OppfoelgingPortType oppfoelgingPortType,
                                                          OrganisasjonEnhetV2Service organisasjonEnhetV2Service) {
        return new OppfolgingsenhetServiceImpl(oppfoelgingPortType, organisasjonEnhetV2Service);
    }

}
