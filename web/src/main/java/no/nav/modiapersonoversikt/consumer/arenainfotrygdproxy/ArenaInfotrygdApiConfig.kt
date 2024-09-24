package no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.exchangeOnBehalfOfToken
import no.nav.personoversikt.common.logging.TjenestekallLogger
import okhttp3.OkHttpClient
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
open class ArenaInfotrygdApiConfig {
    private val scope = DownstreamApi.parse(getRequiredProperty("MODIAPERSONOVERSIKT_API_PROXY_SCOPE"))
    private val url: String = getRequiredProperty("MODIAPERSONOVERSIKT_API_PROXY_URL")

    @Bean
    open fun arenaInfotrygdApi(
        oboTokenProvider: OnBehalfOfTokenClient,
        unleashService: UnleashService,
        tjenestekallLogger: TjenestekallLogger,
    ): ArenaInfotrygdApi {
        val httpClient: OkHttpClient =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(XCorrelationIdInterceptor())
                .connectTimeout(15L, TimeUnit.SECONDS)
                .readTimeout(15L, TimeUnit.SECONDS)
                .writeTimeout(15L, TimeUnit.SECONDS)
                .addInterceptor(
                    LoggingInterceptor(unleashService, "ArenaInfotrygdApi", tjenestekallLogger) { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                ).addInterceptor(
                    AuthorizationInterceptor {
                        oboTokenProvider.exchangeOnBehalfOfToken(scope, AuthContextUtils.requireToken())
                    },
                ).build()
        return ArenaInfotrygdApiImpl(url, httpClient)
    }
}
