package no.nav.modiapersonoversikt.service.sakstema

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SakstemaServiceConfig {
    @Bean
    open fun sakstemaService(): SakstemaService {
        return SakstemaService()
    }
}
