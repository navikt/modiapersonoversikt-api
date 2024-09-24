package no.nav.modiapersonoversikt.config.interceptor

import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.personoversikt.common.logging.TjenestekallLogger
import okhttp3.Request
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class InterceptorConfig {
    @Bean
    open fun loggingInterceptorFactory(
        unleashService: UnleashService,
        tjenestekallLogger: TjenestekallLogger,
    ): TjenestekallLoggingInterceptorFactory =
        { name, interceptor ->
            LoggingInterceptor(unleashService, name, tjenestekallLogger, interceptor)
        }
}

typealias TjenestekallLoggingInterceptorFactory = (name: String, interceptor: (Request) -> String) -> LoggingInterceptor
