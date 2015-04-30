package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.ConsumerContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache.CacheConfiguration;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.PlukkOppgaveService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.PlukkOppgaveServiceImpl;
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

}
