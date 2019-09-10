package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.ConsumerContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.GrunninfoService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache.CacheConfiguration;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.utils.WicketInjectablePropertyResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.GenerellContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Tilgangskontroll;
import no.nav.sbl.modiabrukerdialog.pep.config.spring.PepConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@Import({
        ConsumerContext.class,
        CacheConfiguration.class,
        PepConfig.class
})
public class ApplicationContextBeans {

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PlukkOppgaveService plukkOppgaveService() {
        return new PlukkOppgaveServiceImpl();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new JacksonConfig().getContext(null);
    }

    @Bean
    public WicketInjectablePropertyResolver wicketInjectablePropertyResolver() {
        return new WicketInjectablePropertyResolver();
    }

    @Bean
    public Tilgangskontroll tilgangskontroll(LDAPService ldapService, GrunninfoService grunninfoService) {
        return new Tilgangskontroll(new GenerellContext(ldapService, grunninfoService));
    }
}
