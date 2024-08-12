package no.nav.modiapersonoversikt.consumer.norg

import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
open class NorgConfig {
    private val url: String = EnvironmentUtils.getRequiredProperty("NORG2_BASEURL")

    @Bean
    open fun norgApi(unleashService: UnleashService): NorgApi {
        val httpClient: OkHttpClient =
            RestClient.baseClient().newBuilder()
                .connectTimeout(30L, TimeUnit.SECONDS)
                .addInterceptor(XCorrelationIdInterceptor())
                .addInterceptor(
                    LoggingInterceptor(unleashService, "Norg2") { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                )
                .build()

        return NorgApiImpl(url, httpClient)
    }
}
