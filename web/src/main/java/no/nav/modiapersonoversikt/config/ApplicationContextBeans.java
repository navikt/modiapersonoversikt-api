package no.nav.modiapersonoversikt.config;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.integration.abac.AbacClient;
import no.nav.modiapersonoversikt.integration.abac.AbacClientConfig;
import no.nav.modiapersonoversikt.legacy.api.service.HenvendelseLesService;
import no.nav.modiapersonoversikt.legacy.api.service.OppgaveBehandlingService;
import no.nav.modiapersonoversikt.legacy.api.service.ldap.LDAPService;
import no.nav.modiapersonoversikt.service.unleash.UnleashService;
import no.nav.modiapersonoversikt.infrastructure.cache.CacheConfiguration;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollContext;
import no.nav.modiapersonoversikt.service.plukkoppgave.PlukkOppgaveService;
import no.nav.modiapersonoversikt.service.plukkoppgave.PlukkOppgaveServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import static no.nav.modiapersonoversikt.legacy.api.utils.RestConstants.MODIABRUKERDIALOG_SYSTEM_USER;
import static no.nav.modiapersonoversikt.legacy.api.utils.RestConstants.MODIABRUKERDIALOG_SYSTEM_USER_PASSWORD;

@Configuration
@Import({
        ConsumerContext.class,
        CacheConfiguration.class
})
public class ApplicationContextBeans {

    private static final String ABAC_PDP_URL = EnvironmentUtils.getRequiredProperty("ABAC_PDP_ENDPOINT_URL");

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PlukkOppgaveService plukkOppgaveService(OppgaveBehandlingService oppgaveBehandlingService, Tilgangskontroll tilgangskontroll) {
        return new PlukkOppgaveServiceImpl(
                oppgaveBehandlingService,
                tilgangskontroll
        );
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonConfig.mapper;
    }

    @Bean
    public AbacClient abacClient() {
        AbacClientConfig config = new AbacClientConfig(MODIABRUKERDIALOG_SYSTEM_USER, MODIABRUKERDIALOG_SYSTEM_USER_PASSWORD, ABAC_PDP_URL);
        return new AbacClient(config);
    }

    @Bean
    public Tilgangskontroll tilgangskontroll(
            AbacClient abacClient,
            LDAPService ldapService,
            GOSYSNAVansatt ansattService, // TODO undersøk om denne kan erstattes med axsys
            HenvendelseLesService henvendelseLesService,
            UnleashService unleashService
    ) {
        TilgangskontrollContext context = new TilgangskontrollContextImpl(
                abacClient,
                ldapService,
                ansattService,
                henvendelseLesService,
                unleashService
        );
        return new Tilgangskontroll(context);
    }
}
