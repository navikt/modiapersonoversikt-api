package no.nav.modiapersonoversikt.config.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import no.nav.common.cxf.StsConfig;
import no.nav.common.sts.NaisSystemUserTokenProvider;
import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.consumer.dkif.consumer.support.DkifServiceImpl;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.egenansatt.EgenAnsattService;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.support.DefaultPersonKjerneinfoService;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.support.KjerneinfoMapper;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.vergemal.VergemalService;
import no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.modiapersonoversikt.infrastructure.content.ContentRetriever;
import no.nav.modiapersonoversikt.service.*;
import no.nav.modiapersonoversikt.legacy.api.service.FodselnummerAktorService;
import no.nav.modiapersonoversikt.legacy.api.service.HenvendelseLesService;
import no.nav.modiapersonoversikt.legacy.api.service.HenvendelseUtsendingService;
import no.nav.modiapersonoversikt.legacy.api.service.OppgaveBehandlingService;
import no.nav.modiapersonoversikt.legacy.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.modiapersonoversikt.legacy.api.service.kodeverk.StandardKodeverk;
import no.nav.modiapersonoversikt.legacy.api.service.ldap.LDAPService;
import no.nav.modiapersonoversikt.legacy.api.service.norg.AnsattService;
import no.nav.modiapersonoversikt.legacy.api.service.oppfolgingsinfo.OppfolgingsenhetService;
import no.nav.modiapersonoversikt.legacy.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService;
import no.nav.modiapersonoversikt.legacy.api.service.psak.PsakService;
import no.nav.modiapersonoversikt.legacy.api.service.saker.GsakKodeverk;
import no.nav.modiapersonoversikt.legacy.api.service.saker.SakerService;
import no.nav.modiapersonoversikt.config.endpoint.kodeverksmapper.Kodeverksmapper;
import no.nav.modiapersonoversikt.service.arbeidsfordeling.ArbeidsfordelingClient;
import no.nav.modiapersonoversikt.service.arbeidsfordeling.ArbeidsfordelingV1ServiceImpl;
import no.nav.modiapersonoversikt.service.henvendelse.DelsvarService;
import no.nav.modiapersonoversikt.service.henvendelse.DelsvarServiceImpl;
import no.nav.modiapersonoversikt.service.kodeverk.GsakKodeverkFraFil;
import no.nav.modiapersonoversikt.service.kodeverk.StandardKodeverkImpl;
import no.nav.modiapersonoversikt.service.kodeverksmapper.KodeverksmapperService;
import no.nav.modiapersonoversikt.service.ldap.LDAPServiceImpl;
import no.nav.modiapersonoversikt.service.ldap.LdapContextProvider;
import no.nav.modiapersonoversikt.service.oppfolgingsinfo.OppfolgingsenhetServiceImpl;
import no.nav.modiapersonoversikt.service.oppgavebehandling.RestOppgaveBehandlingServiceImpl;
import no.nav.modiapersonoversikt.service.organisasjonenhet.OrganisasjonEnhetV2ServiceImpl;
import no.nav.modiapersonoversikt.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonService;
import no.nav.modiapersonoversikt.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonServiceImpl;
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagServiceImpl;
import no.nav.modiapersonoversikt.service.saker.SakerServiceImpl;
import no.nav.modiapersonoversikt.service.saker.mediation.SakApiGatewayImpl;
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService;
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseServiceImpl;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import static no.nav.modiapersonoversikt.legacy.api.utils.RestConstants.SECURITY_TOKEN_SERVICE_DISCOVERYURL;

/**
 * MODIA ønsker å selv wire inn sine komponenters kontekster for å ha full kontroll over springoppsettet.
 */
@Configuration
@EnableScheduling
public class ServiceConfig {
    public static final String SYSTEMUSER_USERNAME = "no.nav.modig.security.systemuser.username";
    public static final String SYSTEMUSER_PASSWORD = "no.nav.modig.security.systemuser.password";

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
                                                                   LDAPService ldapService,
                                                                   CacheManager cacheManager) {

        return new HenvendelseUtsendingServiceImpl(henvendelsePortType, sendUtHenvendelsePortType,
                behandleHenvendelsePortType, oppgaveBehandlingService, sakerService, tilgangskontroll,
                propertyResolver, personKjerneinfoServiceBi, ldapService, cacheManager);
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
    public OppgaveBehandlingService oppgaveBehandlingService(
            KodeverksmapperService kodeverksmapperService,
            FodselnummerAktorService fodselnummerAktorService,
            AnsattService ansattService,
            ArbeidsfordelingV1Service arbeidsfordelingV1Service,
            Tilgangskontroll tilgangskontroll,
            SystemUserTokenProvider stsService
    ) {
        return RestOppgaveBehandlingServiceImpl.create(
                kodeverksmapperService,
                fodselnummerAktorService,
                ansattService,
                arbeidsfordelingV1Service,
                tilgangskontroll,
                stsService
        );
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
    public SakApiGatewayImpl sakApiGateway(SystemUserTokenProvider stsService) {
        return new SakApiGatewayImpl(
                EnvironmentUtils.getRequiredProperty("SAK_ENDPOINTURL"),
                stsService
        );
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
        return new NaisSystemUserTokenProvider(
                SECURITY_TOKEN_SERVICE_DISCOVERYURL,
                EnvironmentUtils.getRequiredProperty(SYSTEMUSER_USERNAME),
                EnvironmentUtils.getRequiredProperty(SYSTEMUSER_PASSWORD)
        );
    }

    @Bean
    StsConfig stsConfig() {
        return StsConfig.builder()
                .url(EnvironmentUtils.getRequiredProperty("SECURITYTOKENSERVICE_URL"))
                .username(EnvironmentUtils.getRequiredProperty(SYSTEMUSER_USERNAME))
                .password(EnvironmentUtils.getRequiredProperty(SYSTEMUSER_PASSWORD))
                .build();
    }

    @Bean
    public FodselnummerAktorService fodselnummerAktorService() {
        return new FodselnummerAktorServiceImpl();
    }

    @Bean
    public PdlOppslagService pdlOppslagService(SystemUserTokenProvider sts) {
        return new PdlOppslagServiceImpl(sts);
    }

    @Bean
    public SfHenvendelseService sfHenvendelseApi(PdlOppslagService pdlOppslagService) {
        return new SfHenvendelseServiceImpl(pdlOppslagService);
    }
}
