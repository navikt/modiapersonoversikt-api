package no.nav.modiapersonoversikt.service.saker

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SakerServiceConfig {
    @Bean
    open fun sakerService(): SakerService = SakerServiceImpl()
}
