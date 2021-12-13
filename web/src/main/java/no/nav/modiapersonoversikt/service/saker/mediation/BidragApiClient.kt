package no.nav.modiapersonoversikt.service.saker.mediation

import no.nav.common.auth.context.AuthContextHolderThreadLocal
import no.nav.common.rest.client.RestClient
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor

object BidragApiClient {
    val client = RestClient.baseClient().newBuilder()
        .addInterceptor(XCorrelationIdInterceptor())
        .addInterceptor(
            LoggingInterceptor("Bisys") { request ->
                requireNotNull(request.header("X-Correlation-ID")) {
                    "Kall uten \"X-Correlation-ID\" er ikke lov"
                }
            }
        )
        .addInterceptor(
            AuthorizationInterceptor {
                AuthContextHolderThreadLocal.instance().requireIdTokenString()
            }
        )
        .build()
}
