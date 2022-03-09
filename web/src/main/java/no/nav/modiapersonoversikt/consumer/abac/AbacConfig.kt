package no.nav.modiapersonoversikt.consumer.abac

import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.legacy.api.utils.RestConstants
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AbacConfig {
    @Bean
    open fun abacClient(): AbacClient {
        return AbacClient(
            username = RestConstants.MODIABRUKERDIALOG_SYSTEM_USER,
            password = RestConstants.MODIABRUKERDIALOG_SYSTEM_USER_PASSWORD,
            endpointUrl = EnvironmentUtils.getRequiredProperty("ABAC_PDP_ENDPOINT_URL")
        )
    }
}
