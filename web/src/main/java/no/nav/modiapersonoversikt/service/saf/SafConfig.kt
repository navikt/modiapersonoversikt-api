package no.nav.modiapersonoversikt.service.saf

import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.HeadersInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.bindTo
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SafConfig {
    val downstreamapi = DownstreamApi.parse(getRequiredProperty("SAF_SCOPE"))

    @Bean
    open fun safService(
        unleashService: UnleashService,
        oboTokenClient: OnBehalfOfTokenClient,
    ): SafService {
        val client: OkHttpClient =
            RestClient.baseClient().newBuilder()
                .addInterceptor(
                    HeadersInterceptor {
                        this.httpHeaders(oboTokenClient.bindTo(downstreamapi))
                    },
                )
                .addInterceptor(
                    LoggingInterceptor(unleashService, "Saf") { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                )
                .build()

        return SafServiceImpl(
            oboTokenClient.bindTo(downstreamapi),
            client,
        )
    }

    private fun httpHeaders(oboTokenProvider: BoundedOnBehalfOfTokenClient): Map<String, String> {
        val token = AuthContextUtils.requireBoundedClientOboToken(oboTokenProvider)
        val callId = getCallId()
        return mapOf(
            "Authorization" to "Bearer $token",
            "Content-Type" to "application/json",
            "X-Correlation-ID" to callId,
        )
    }
}
