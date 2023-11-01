package no.nav.modiapersonoversikt.service.utbetaling

import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.apis.UtbetaldataV2Api
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
open class UtbetalingServiceConfig {
    @Bean
    open fun utbetalingerService(restClient: UtbetaldataV2Api, unleash: UnleashService): UtbetalingService =
        UtbetalingServiceImpl(restClient, unleash)
}
