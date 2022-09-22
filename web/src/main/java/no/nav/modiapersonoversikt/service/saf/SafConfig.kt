package no.nav.modiapersonoversikt.service.saf

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SafConfig {
    @Bean
    open fun safService(): SafService = SafServiceImpl()
}
