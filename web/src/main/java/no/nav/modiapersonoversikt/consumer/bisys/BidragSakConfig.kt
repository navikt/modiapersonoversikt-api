package no.nav.modiapersonoversikt.consumer.bisys

import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.apis.BidragSakControllerApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class BidragSakConfig {
    companion object {
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
                    AuthContextUtils.requireToken()
                }
            )
            .build()
    }

    @Bean
    open fun bidragSakControllerApi() = BidragSakControllerApi(
        basePath = EnvironmentUtils.getRequiredProperty("BISYS_BASEURL"),
        httpClient = client
    )
}
