package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modig.content.PropertyResolver;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.wicket.services.HealthCheckService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.PsakServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverksmapper.Kodeverksmapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.oppgavebehandling.OppgaveBehandlingServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.SvarDelvisService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.SvarDelvisServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk.GsakKodeverkFraFil;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk.StandardKodeverkImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ldap.LDAPServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.OrganisasjonEnhetV2ServiceImpl;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.ArbeidsfordelingV1;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
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
    public SvarDelvisService HenvendelseService(HenvendelseUtsendingService henvendelseUtsendingService) {
        return new SvarDelvisServiceImpl(henvendelseUtsendingService);
    }

    @Bean
    public KodeverksmapperService kodeverksmapperService(Kodeverksmapper kodeverksmapper) {
        return new KodeverksmapperService(kodeverksmapper);
    }

    @Bean
    public ArbeidsfordelingV1Service arbeidsfordelingV1Service(ArbeidsfordelingV1 arbeidsfordeling, PersonKjerneinfoServiceBi personService,
                                                               KodeverksmapperService kodeverksmapper) {
        return new ArbeidsfordelingV1Service(arbeidsfordeling, personService, kodeverksmapper);
    }

    @Bean
    public OppgaveBehandlingService oppgaveBehandlingService(OppgavebehandlingV3 oppgavebehandlingV3, OppgaveV3 oppgaveV3,
                                                             AnsattService ansattService, Ruting ruting,
                                                             ArbeidsfordelingV1Service arbeidsfordelingV1Service) {
        return new OppgaveBehandlingServiceImpl(oppgavebehandlingV3, oppgaveV3, ansattService, ruting, arbeidsfordelingV1Service);
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

}
