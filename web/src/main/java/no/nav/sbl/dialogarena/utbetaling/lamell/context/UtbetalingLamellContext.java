package no.nav.sbl.dialogarena.utbetaling.lamell.context;

import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtbetalingLamellContext {

    @Value("${SERVER_ARENA_URL}")
    private String arenaServerUrl;

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
