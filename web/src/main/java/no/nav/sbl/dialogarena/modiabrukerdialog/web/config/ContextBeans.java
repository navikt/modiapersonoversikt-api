package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.ConsumerContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@Import({
        ConsumerContext.class,
        CacheConfig.class
})
public class ContextBeans {
    private static final Logger LOG = LoggerFactory.getLogger(ContextBeans.class);

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public WicketApplication modiaApplication() {
        LOG.debug("ContextBeans modiaapp");
        return new WicketApplication();
    }

}
