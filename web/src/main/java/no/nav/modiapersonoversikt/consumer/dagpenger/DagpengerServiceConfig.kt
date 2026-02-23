package no.nav.modiapersonoversikt.consumer.dagpenger

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
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
open class DagpengerServiceConfig {
    private val scope = DownstreamApi.Companion.parse(getRequiredProperty("DAGPENGER_SCOPE"))
    private val url: String = getRequiredProperty("DAGPENGER_URL")

    @Bean
    open fun dagpengerService(
        machineToMachineTokenClient: MachineToMachineTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): DagpengerService {
        val httpClient: OkHttpClient =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(XCorrelationIdInterceptor())
                .addInterceptor(
                    tjenestekallLoggingInterceptorFactory("DagpengerApi") { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                ).addInterceptor(
                    AuthorizationInterceptor {
                        machineToMachineTokenClient.createMachineToMachineToken(scope)
                    },
                ).build()
        return DagpengerServiceImpl(url, httpClient)
    }
}
