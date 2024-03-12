package no.nav.modiapersonoversikt.consumer.norg

import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class NorgConfig {
    private val url: String = EnvironmentUtils.getRequiredProperty("NORG2_BASEURL")
    private val httpClient: OkHttpClient =
        RestClient.baseClient().newBuilder()
            .addInterceptor(XCorrelationIdInterceptor())
            .addInterceptor(
                LoggingInterceptor("Norg2") { request ->
                    requireNotNull(request.header("X-Correlation-ID")) {
                        "Kall uten \"X-Correlation-ID\" er ikke lov"
                    }
                },
            )
            .build()

    @Bean
    open fun norgApi(): NorgApi = NorgApiImpl(url, httpClient)
}
