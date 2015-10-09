package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class UtbetalingerMockContext {

    @Bean
    public UtbetalingService utbetalingsService() {
        return mock(UtbetalingService.class);
    }
}
