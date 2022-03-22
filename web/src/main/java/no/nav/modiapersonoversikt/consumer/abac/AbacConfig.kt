package no.nav.modiapersonoversikt.consumer.abac

import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.config.AppConstants
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AbacConfig {
    @Bean
    open fun abacClient(): AbacClient {
        return AbacClient(
            username = AppConstants.SYSTEMUSER_USERNAME,
            password = AppConstants.SYSTEMUSER_PASSWORD,
            endpointUrl = EnvironmentUtils.getRequiredProperty("ABAC_PDP_ENDPOINT_URL")
        )
    }
}
