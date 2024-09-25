package no.nav.modiapersonoversikt.config.logg

import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.personoversikt.common.logging.TjenestekallLogg
import no.nav.personoversikt.common.logging.TjenestekallLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class LoggConfig {
    @Bean
    open fun tjenestekallLogger(unleashService: UnleashService): TjenestekallLogger =
        UnleashTjenestekallLogger(
            TjenestekallLogg,
            unleashService,
        )
}
