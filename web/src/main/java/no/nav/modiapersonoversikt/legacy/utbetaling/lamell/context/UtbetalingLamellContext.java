package no.nav.modiapersonoversikt.legacy.utbetaling.lamell.context;

import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.legacy.utbetaling.service.UtbetalingService;
import no.nav.modiapersonoversikt.legacy.utbetaling.service.UtbetalingServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtbetalingLamellContext {
    private static final String arenaServerUrl = EnvironmentUtils.getRequiredProperty("SERVER_ARENA_URL");
    private static final String ARENA_MELDINGER_UTBETALINGER = "?oppstart_skj=UB_22_MELDEHISTORIKK&fodselsnr=";

    @Bean(name = "arenaUtbetalingUrl")
    public String arenaUtbetalingUrl() {
        return arenaServerUrl + ARENA_MELDINGER_UTBETALINGER;
    }

    @Bean
    public UtbetalingService utbetalingsService() {
        return new UtbetalingServiceImpl();
    }

}
