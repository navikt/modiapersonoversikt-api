package no.nav.modiapersonoversikt.consumer.sak

import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SakApiConfig {
    @Bean
    open fun sakApi(stsService: SystemUserTokenProvider): SakApi {
        return SakApiImpl(
            EnvironmentUtils.getRequiredProperty("SAK_ENDPOINTURL"),
            stsService
        )
    }
}
