package no.nav.sbl.dialogarena.utbetaling.lamell.context;

import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingsHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UbetalingLamellContext {

    @Value("${server.arena.url}")
    private String arenaServerUrl;

    private String arenaMeldingerUtbetalinger = "?oppstart_skj=UB_22_MELDEHISTORIKK&fodselsnr=";

    @Bean
    public String arenaUtbetalingUrl() {
        return arenaServerUrl + arenaMeldingerUtbetalinger;
    }

    @Bean
    public UtbetalingService utbetalingsService() {
        return new UtbetalingService();
    }

    @Bean
    public UtbetalingsHolder utbetalingsHolder() {
        return new UtbetalingsHolder();
    }

}
