package no.nav.modiapersonoversikt.consumer.pdlPip

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import no.nav.personoversikt.common.logging.TjenestekallLogger
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PdlPipConfig {
    private val scope = DownstreamApi.parse(getRequiredProperty("PDL_PIP_SCOPE"))
    private val url: String = getRequiredProperty("PDL_PIP_URL")

    @Bean
    open fun pdlPip(
        tokenProvider: MachineToMachineTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
        tjenestekallLogger: TjenestekallLogger,
    ): PdlPipApi {
        val httpClient: OkHttpClient =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(XCorrelationIdInterceptor())
                .addInterceptor(
                    tjenestekallLoggingInterceptorFactory("PdlPipApi") { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                ).addInterceptor(
                    AuthorizationInterceptor {
                        tokenProvider.createMachineToMachineToken(scope)
                    },
                ).build()
        return PdlPipApiImpl(url, httpClient, tjenestekallLogger)
    }
}
