package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.common.utils.EnvironmentUtils;
import static no.nav.common.utils.EnvironmentUtils.Type.PUBLIC;
import no.nav.sbl.dialogarena.abac.AbacClient;
import no.nav.sbl.dialogarena.abac.AbacClientConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseLesService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.ConsumerContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache.CacheConfiguration;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.MODIABRUKERDIALOG_SYSTEM_USER;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.MODIABRUKERDIALOG_SYSTEM_USER_PASSWORD;

@Configuration
@Import({
        ConsumerContext.class,
        CacheConfiguration.class
})
public class ApplicationContextBeans {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContextBeans.java);
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

    @Bean
    EnableCXFSecureLogs enableCXFSecureLogs() {
        try {
            EnvironmentUtils.setProperty("CXF_SECURE_LOG", "enabled", PUBLIC);
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            context.reset();
            ContextInitializer ci = new ContextInitializer(context);
            ci.autoConfig();
        } catch (JoranException e) {
            throw new RuntimeException("Failed to enable CXF secure logs", e);
        }

        return this;
    }
}
