package no.nav.modiapersonoversikt.config.interceptor

import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.personoversikt.common.logging.TjenestekallLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class InterceptorConfig {
    @Bean
    open fun tjenestekallLoggingInterceptorFactory(
        unleashService: UnleashService,
        tjenestekallLogger: TjenestekallLogger,
    ): TjenestekallLoggingInterceptorFactory =
        { name, interceptor ->
            LoggingInterceptor(unleashService, tjenestekallLogger, name, interceptor)
        }
}
