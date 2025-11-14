package no.nav.modiapersonoversikt.consumer.fpsak

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import okhttp3.OkHttpClient
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
open class FpSakConfig {
    private val scope = DownstreamApi.Companion.parse(EnvironmentUtils.getRequiredProperty("FP_SAK_SCOPE"))
    private val url: String = EnvironmentUtils.getRequiredProperty("FP_SAK_URL")

    @Bean
    open fun fpSakClient(
        machineToMachineTokenClient: MachineToMachineTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): FpSakClient {
        val httpClient: OkHttpClient =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(XCorrelationIdInterceptor())
                .addInterceptor(
                    tjenestekallLoggingInterceptorFactory("FpSakApi") { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                ).addInterceptor(
                    AuthorizationInterceptor {
                        machineToMachineTokenClient.createMachineToMachineToken(scope)
                    },
                ).build()
        return FpSakClientImpl(url, httpClient)
    }
}