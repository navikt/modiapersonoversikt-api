package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.HeadersInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.exchangeOnBehalfOfToken
import no.nav.personoversikt.common.logging.TjenestekallLogger
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class BrukernotifikasjonConfig {
    private val tmsEventApiUrl: String = getRequiredProperty("TMS_EVENT_API_URL")
    private val tmsEventApiApi: DownstreamApi = DownstreamApi.parse(getRequiredProperty("TMS_EVENT_API_SCOPE"))

    @Bean
    open fun brukernotifikasjonService(
        oboTokenProvider: OnBehalfOfTokenClient,
        unleashService: UnleashService,
        tjenestekallLogger: TjenestekallLogger,
    ): Brukernotifikasjon.Service {
        val authInterceptor =
            HeadersInterceptor {
                val azureAdToken = AuthContextUtils.requireToken()
                val oboToken = oboTokenProvider.exchangeOnBehalfOfToken(tmsEventApiApi, azureAdToken)
                mapOf("Authorization" to "Bearer $oboToken")
            }

        val httpClient: OkHttpClient =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(XCorrelationIdInterceptor())
                .addInterceptor(
                    LoggingInterceptor(unleashService, "Brukernotifikasjon", tjenestekallLogger) { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                ).addInterceptor(authInterceptor)
                .build()
        return BrukernotifikasjonService(
            BrukernotifikasjonClient(
                baseUrl = tmsEventApiUrl,
                httpClient = httpClient,
            ),
        )
    }
}
