package no.nav.modiapersonoversikt.service.saker.mediation

import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.apis.BidragSakControllerApi

object BidragApiFactory {
    fun createClient(tokenProvider: () -> String): BidragSakControllerApi {
        val url = EnvironmentUtils.getRequiredProperty("BISYS_BASEURL")
        val client = RestClient.baseClient().newBuilder()
            .addInterceptor(
                LoggingInterceptor("Bisys") { request ->
                    requireNotNull(request.header("X-Correlation-ID")) {
                        "Kall uten \"X-Correlation-ID\" er ikke lov"
                    }
                }
            )
            .addInterceptor(AuthorizationInterceptor(tokenProvider))
            .build()
        return BidragSakControllerApi(url, client)
    }
}

