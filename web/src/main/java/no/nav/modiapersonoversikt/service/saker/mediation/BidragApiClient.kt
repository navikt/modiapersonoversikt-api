package no.nav.modiapersonoversikt.service.saker.mediation

import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.apis.BidragSakControllerApi

interface BidragApiClient {
    fun createClient(tokenProvider: () -> String): BidragSakControllerApi
}

class BidragApiClientImpl(
    private val baseUrl: String = EnvironmentUtils.getRequiredProperty("BISYS_ENDPOINTURL")
) : BidragApiClient {

    override
    fun createClient(tokenProvider: () -> String): BidragSakControllerApi {
        val client = RestClient.baseClient().newBuilder()
            .addInterceptor(XCorrelationIdInterceptor())
            .addInterceptor(
                LoggingInterceptor("Bisys") { request ->
                    requireNotNull(request.header("X-Correlation-ID")) {
                        "Kall uten \"X-Correlation-ID\" er ikke lov"
                    }
                }
            )
            .addInterceptor(AuthorizationInterceptor(tokenProvider))
            .build()
        return BidragSakControllerApi(baseUrl, client)
    }
}
