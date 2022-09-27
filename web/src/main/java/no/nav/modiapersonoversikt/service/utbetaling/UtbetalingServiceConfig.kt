package no.nav.modiapersonoversikt.service.utbetaling

import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.apis.UtbetaldataV2Api
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class UtbetalingServiceConfig {
    @Bean
    open fun utbetalingerService(restClient: UtbetaldataV2Api): UtbetalingService =
        UtbetalingServiceImpl(restClient)
}
