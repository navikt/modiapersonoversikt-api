package no.nav.modiapersonoversikt.config.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import no.nav.common.cxf.StsConfig;
import no.nav.common.sts.NaisSystemUserTokenProvider;
import no.nav.common.sts.ServiceToServiceTokenProvider;
import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.common.sts.utils.AzureAdServiceTokenProviderBuilder;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.apis.BidragSakControllerApi;
import no.nav.modiapersonoversikt.infrastructure.content.ContentRetriever;
import no.nav.modiapersonoversikt.rest.persondata.PersondataService;
import no.nav.modiapersonoversikt.service.*;
import no.nav.modiapersonoversikt.legacy.api.service.HenvendelseLesService;
import no.nav.modiapersonoversikt.legacy.api.service.HenvendelseUtsendingService;
import no.nav.modiapersonoversikt.legacy.api.service.OppgaveBehandlingService;
import no.nav.modiapersonoversikt.legacy.api.service.ldap.LDAPService;
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService;
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService;
import no.nav.modiapersonoversikt.legacy.api.service.psak.PsakService;
import no.nav.modiapersonoversikt.legacy.api.service.saker.GsakKodeverk;
import no.nav.modiapersonoversikt.legacy.api.service.saker.SakerService;
import no.nav.modiapersonoversikt.config.endpoint.kodeverksmapper.Kodeverksmapper;
import no.nav.modiapersonoversikt.service.AnsattServiceImpl;
import no.nav.modiapersonoversikt.service.arbeidsfordeling.ArbeidsfordelingService;
import no.nav.modiapersonoversikt.service.dkif.Dkif;
import no.nav.modiapersonoversikt.service.dkif.DkifServiceImpl;
import no.nav.modiapersonoversikt.service.dkif.DkifServiceRestImpl;
import no.nav.modiapersonoversikt.service.henvendelse.DelsvarService;
import no.nav.modiapersonoversikt.service.henvendelse.DelsvarServiceImpl;
import no.nav.modiapersonoversikt.service.kodeverk.GsakKodeverkFraFil;
import no.nav.modiapersonoversikt.service.kodeverksmapper.KodeverksmapperService;
import no.nav.modiapersonoversikt.service.ldap.LDAPServiceImpl;
import no.nav.modiapersonoversikt.service.ldap.LdapContextProvider;
import no.nav.modiapersonoversikt.service.oppgavebehandling.RestOppgaveBehandlingServiceImpl;
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagServiceImpl;
import no.nav.modiapersonoversikt.service.saker.SakerServiceImpl;
import no.nav.modiapersonoversikt.service.saker.mediation.BidragApiClient;
import no.nav.modiapersonoversikt.service.saker.mediation.SakApiGatewayImpl;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseApiFactory;
import no.nav.modiapersonoversikt.service.unleash.UnleashService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
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
                                                                   PersondataService persondataService,
                                                                   LDAPService ldapService,
                                                                   CacheManager cacheManager) {

        return new HenvendelseUtsendingServiceImpl(henvendelsePortType, sendUtHenvendelsePortType,
                behandleHenvendelsePortType, oppgaveBehandlingService, sakerService, tilgangskontroll,
                propertyResolver, persondataService, ldapService, cacheManager);
    }

    @Bean
    public HenvendelseLesService henvendelseLesService(
            SystemUserTokenProvider systemUserTokenProvider,
            UnleashService unleashService
    ) {
        return new HenvendelseLesServiceImpl(
                systemUserTokenProvider,
                SfHenvendelseApiFactory.INSTANCE.createHenvendelseInfoApi(),
                unleashService
        );
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
    public OppgaveBehandlingService oppgaveBehandlingService(
            KodeverksmapperService kodeverksmapperService,
            PdlOppslagService pdlOppslagService,
            AnsattService ansattService,
            ArbeidsfordelingService arbeidsfordelingService,
            Tilgangskontroll tilgangskontroll,
            SystemUserTokenProvider stsService
    ) {
        return RestOppgaveBehandlingServiceImpl.create(
                kodeverksmapperService,
                pdlOppslagService,
                ansattService,
                arbeidsfordelingService,
                tilgangskontroll,
                stsService
        );
    }

    @Bean
    public AnsattService ansattService(GOSYSNAVansatt gosysNavAnsatt) {
        return new AnsattServiceImpl(gosysNavAnsatt);
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
    public BidragSakControllerApi bidragSakControllerApi() {
        return new BidragSakControllerApi(
                EnvironmentUtils.getRequiredProperty("BISYS_BASEURL"),
                BidragApiClient.INSTANCE.getClient()
        );
    }

    @Bean
    public PsakService psakService(PensjonSakV1 pensjonSakV1) {
        return new PsakServiceImpl(pensjonSakV1);
    }

    @Bean(name = "DkifSoap")
    public Dkif.Service defaultDkifService(DigitalKontaktinformasjonV1 dkifV1) {
        return new DkifServiceImpl(dkifV1);
    }

    @Bean(name = "DkifRest")
    public Dkif.Service restDkifService() {
        return new DkifServiceRestImpl(
                EnvironmentUtils.getRequiredProperty("DKIF_REST_URL")
        );
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
    public ServiceToServiceTokenProvider serviceToServiceTokenProvider() {
        return AzureAdServiceTokenProviderBuilder.builder()
                .withEnvironmentDefaults()
                .build();
    }

    @Bean
    public PdlOppslagService pdlOppslagService(SystemUserTokenProvider sts) {
        return new PdlOppslagServiceImpl(sts);
    }

}
