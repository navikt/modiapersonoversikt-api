package no.nav.sbl.dialogarena.utbetaling.context;

import no.nav.sbl.dialogarena.utbetaling.UtbetalingApplication;
import no.nav.sbl.dialogarena.utbetaling.config.UtbetalingConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@Import({UtbetalingConfig.class})
public class JettyApplicationContext {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public UtbetalingApplication application() {
        return new UtbetalingApplication();
    }

}
