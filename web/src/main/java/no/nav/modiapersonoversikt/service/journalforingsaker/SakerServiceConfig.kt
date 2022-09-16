package no.nav.modiapersonoversikt.service.journalforingsaker

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SakerServiceConfig {
    @Bean
    open fun sakerService(): SakerService = SakerServiceImpl()
}
