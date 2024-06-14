package no.nav.modiapersonoversikt.consumer.norg

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
open class NorgConfig {
    private val scope = DownstreamApi.parse(EnvironmentUtils.getRequiredProperty("NORG2_SCOPE"))
    private val url: String = EnvironmentUtils.getRequiredProperty("NORG2_BASEURL")

    @Bean
    open fun norgApi(tokenClient: MachineToMachineTokenClient): NorgApi {
        val httpClient: OkHttpClient =
            RestClient.baseClient().newBuilder()
                .connectTimeout(30L, TimeUnit.SECONDS)
                .addInterceptor(XCorrelationIdInterceptor())
                .addInterceptor(
                    LoggingInterceptor("Norg2") { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                )
                .addInterceptor(
                    AuthorizationInterceptor {
                        tokenClient.createMachineToMachineToken(scope)
                    },
                )
                .build()

        return NorgApiImpl(url, httpClient)
    }
}
