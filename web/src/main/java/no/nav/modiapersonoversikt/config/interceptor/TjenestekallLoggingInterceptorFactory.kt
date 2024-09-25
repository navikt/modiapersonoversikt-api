package no.nav.modiapersonoversikt.config.interceptor

import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import okhttp3.Request

typealias TjenestekallLoggingInterceptorFactory = (name: String, interceptor: (Request) -> String) -> LoggingInterceptor
