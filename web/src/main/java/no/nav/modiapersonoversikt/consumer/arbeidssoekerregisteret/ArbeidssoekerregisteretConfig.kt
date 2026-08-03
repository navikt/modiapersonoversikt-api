package no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret.generated.apis.DefaultApi
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.exchangeOnBehalfOfToken
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ArbeidssoekerregisteretConfig {
    private val scope = DownstreamApi.parse(getRequiredProperty("ARBEIDSSOEKERREGISTERET_SCOPE"))
    private val url: String = getRequiredProperty("ARBEIDSSOEKERREGISTERET_URL")

    @Bean
    open fun arbeidssoekerregisteretService(
        oboTokenProvider: OnBehalfOfTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): ArbeidssoekerregisteretService {
        val httpClient: OkHttpClient =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(XCorrelationIdInterceptor())
                .addInterceptor(
                    tjenestekallLoggingInterceptorFactory("ArbeidssoekerregisteretApi") { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                ).addInterceptor(
                    AuthorizationInterceptor {
                        oboTokenProvider.exchangeOnBehalfOfToken(scope, AuthContextUtils.requireToken())
                    },
                ).build()
        return ArbeidssoekerregisteretServiceImpl(DefaultApi(url, httpClient))
    }
}
