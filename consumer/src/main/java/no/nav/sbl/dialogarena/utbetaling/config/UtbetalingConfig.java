package no.nav.sbl.dialogarena.utbetaling.config;

import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingsHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtbetalingConfig {

    @Bean
    public UtbetalingService utbetalingsService() {
        return new UtbetalingService();
    }

    @Bean
    public UtbetalingsHolder utbetalingsHolder() {
        return new UtbetalingsHolder();
    }
}
