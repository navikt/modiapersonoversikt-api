package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;

import javax.inject.Named;

import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.dkif.consumer.support.DkifServiceImpl;
import no.nav.kjerneinfo.consumer.egenansatt.EgenAnsattService;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.support.DefaultPersonKjerneinfoService;
import no.nav.kjerneinfo.consumer.fim.person.support.KjerneinfoMapper;
import no.nav.kjerneinfo.consumer.fim.person.vergemal.VergemalService;
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.modig.content.ContentRetriever;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseLesService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveRestClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsenhetService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverksmapper.Kodeverksmapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.AnsattServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseLesServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.PsakServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ScheduledAnsattListePrefetch;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling.ArbeidsfordelingClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling.ArbeidsfordelingV1ServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.DelsvarService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.DelsvarServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk.GsakKodeverkFraFil;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk.StandardKodeverkImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ldap.LDAPServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ldap.LdapContextProvider;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo.OppfolgingsenhetServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgave.OppgaveOpprettelseClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.OppgaveBehandlingServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.OrganisasjonEnhetV2ServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl.PdlOppslagServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.TildelOppgaveV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import static no.nav.sbl.dialogarena.common.cxf.StsSecurityConstants.SYSTEMUSER_PASSWORD;
import static no.nav.sbl.dialogarena.common.cxf.StsSecurityConstants.SYSTEMUSER_USERNAME;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.SECURITY_TOKEN_SERVICE_DISCOVERYURL;
import static no.nav.sbl.util.EnvironmentUtils.*;

/**
 * MODIA ønsker å selv wire inn sine komponenters kontekster for å ha full kontroll over springoppsettet.
 */
@Configuration
@EnableScheduling
public class ServiceConfig {

    @Bean
    public LDAPService ldapService() {
        return new LDAPServiceImpl(new LdapContextProvider());
    }

    @Bean
    public HenvendelseUtsendingService henvendelseUtsendingService(HenvendelsePortType henvendelsePortType,
                                                                   SendUtHenvendelsePortType sendUtHenvendelsePortType,
                                                                   BehandleHenvendelsePortType behandleHenvendelsePortType,
                                                                   OppgaveBehandlingService oppgaveBehandlingService,
                                                                   SakerService sakerService,
                                                                   Tilgangskontroll tilgangskontroll,
                                                                   ContentRetriever propertyResolver,
                                                                   PersonKjerneinfoServiceBi personKjerneinfoServiceBi,
                                                                   LDAPService ldapService) {

        return new HenvendelseUtsendingServiceImpl(henvendelsePortType, sendUtHenvendelsePortType,
                behandleHenvendelsePortType, oppgaveBehandlingService, sakerService, tilgangskontroll,
                propertyResolver, personKjerneinfoServiceBi, ldapService);
    }

    @Bean
    public HenvendelseLesService henvendelseLesService(SystemUserTokenProvider systemUserTokenProvider) {
        return new HenvendelseLesServiceImpl(systemUserTokenProvider);
    }

    @Bean
    public DelsvarService HenvendelseService(HenvendelseUtsendingService henvendelseUtsendingService, OppgaveBehandlingService oppgaveBehandlingService) {
        return new DelsvarServiceImpl(henvendelseUtsendingService, oppgaveBehandlingService);
    }

    @Bean
    public OppgaveRestClient oppgaveOpprettelseClient(KodeverksmapperService kodeverksmapperService, PdlOppslagService pdlOppslagService) {
        return new OppgaveOpprettelseClient(kodeverksmapperService, pdlOppslagService);
    }

    @Bean
    public KodeverksmapperService kodeverksmapperService(Kodeverksmapper kodeverksmapper) {
        return new KodeverksmapperService(kodeverksmapper);
    }

    @Bean
    public ArbeidsfordelingClient arbeidsfordelingClient() {
        return new ArbeidsfordelingClient();
    }

    @Bean
    public ArbeidsfordelingV1Service arbeidsfordelingV1Service(ArbeidsfordelingClient arbeidsfordelingClient, EgenAnsattService egenAnsattService, PersonKjerneinfoServiceBi personService, KodeverksmapperService kodeverksmapper) {
        return new ArbeidsfordelingV1ServiceImpl(arbeidsfordelingClient, egenAnsattService, personService, kodeverksmapper);
    }

    @Bean
    public OppgaveBehandlingService oppgaveBehandlingService(OppgavebehandlingV3 oppgavebehandlingV3,
                                                             TildelOppgaveV1 tildelOppgaveV1,
                                                             OppgaveV3 oppgaveV3,
                                                             AnsattService ansattService,
                                                             ArbeidsfordelingV1Service arbeidsfordelingV1Service) {
        return new OppgaveBehandlingServiceImpl(oppgavebehandlingV3, tildelOppgaveV1, oppgaveV3, ansattService, arbeidsfordelingV1Service);
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
    public OrganisasjonEnhetKontaktinformasjonService organisasjonEnhetKontaktinformasjon(OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1) {
        return new OrganisasjonEnhetKontaktinformasjonServiceImpl(organisasjonEnhetKontaktinformasjonV1);
    }

    @Bean
    public OppfolgingsenhetService oppfolgingsenhetService(OppfoelgingPortType oppfoelgingPortType,
                                                           OrganisasjonEnhetV2Service organisasjonEnhetV2Service) {
        return new OppfolgingsenhetServiceImpl(oppfoelgingPortType, organisasjonEnhetV2Service);
    }

    @Bean
    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi(PersonV3 personPortType, KjerneinfoMapper kjerneinfoMapper,
                                                               Tilgangskontroll tilgangskontroll, OrganisasjonEnhetV2Service organisasjonEnhetV2Service) {
        return new DefaultPersonKjerneinfoService(personPortType, kjerneinfoMapper, tilgangskontroll, organisasjonEnhetV2Service);
    }

    @Bean
    public VergemalService vergemalService(
            PersonV3 personPortType,
            PdlOppslagService pdl,
            KodeverkmanagerBi kodeverkmanagerBi
    ) {
        return new VergemalService(personPortType, pdl, kodeverkmanagerBi);
    }

    @Bean
    DkifServiceImpl defaultDkifService(DigitalKontaktinformasjonV1 dkifV1) {
        return new DkifServiceImpl(dkifV1);
    }

    @Bean
    SystemUserTokenProvider systemUserTokenProvider() {
        return new SystemUserTokenProvider(
                SECURITY_TOKEN_SERVICE_DISCOVERYURL,
                getRequiredProperty(SYSTEMUSER_USERNAME, resolveSrvUserPropertyName()),
                getRequiredProperty(SYSTEMUSER_PASSWORD, resolverSrvPasswordPropertyName())
        );
    }

    @Bean
    PdlOppslagService pdlOppslagService(SystemUserTokenProvider sts) {
        return new PdlOppslagServiceImpl(sts);
    }
}
