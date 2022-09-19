package no.nav.modiapersonoversikt.service.utbetaling;

import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.apis.UtbetaldataV2Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtbetalingServiceConfig {
    private static final String arenaServerUrl = EnvironmentUtils.getRequiredProperty("SERVER_ARENA_URL");
    private static final String ARENA_MELDINGER_UTBETALINGER = "?oppstart_skj=UB_22_MELDEHISTORIKK&fodselsnr=";

    @Bean(name = "arenaUtbetalingUrl")
    public String arenaUtbetalingUrl() {
        return arenaServerUrl + ARENA_MELDINGER_UTBETALINGER;
    }

    @Bean
    public WSUtbetalingService wsUtbetalingsService() {
        return new WSUtbetalingServiceImpl();
    }

    @Bean
    public UtbetalingService restUtbetalingService(UtbetaldataV2Api apiClient) {
        return new UtbetalingServiceImpl(apiClient);
    }
}
