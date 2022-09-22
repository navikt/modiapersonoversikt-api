package no.nav.modiapersonoversikt.legacy.sak.config

import no.nav.modiapersonoversikt.legacy.sak.service.SakOgBehandlingService
import no.nav.modiapersonoversikt.legacy.sak.service.SakstemaService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SakServiceConfig {
    @Bean
    open fun sakOgBehandlingService(): SakOgBehandlingService {
        return SakOgBehandlingService()
    }

    @Bean
    open fun sakstemaService(): SakstemaService {
        return SakstemaService()
    }
}
