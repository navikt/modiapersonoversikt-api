package no.nav.modiapersonoversikt.consumer.tiltakspenger

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.consumer.tiltakspenger.generated.apis.TiltakspengerApi
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.HeadersInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.bindTo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TiltakspengerConfig {
    private val basePath = getRequiredProperty("TILTAKSPENGER_URL")
    private val scope = DownstreamApi.parse(getRequiredProperty("TILTAKSPENGER_SCOPE"))

    @Bean
    open fun tiltakspengerApi(
        machineTokenClient: MachineToMachineTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): TiltakspengerService {
        val tokenProvider = machineTokenClient.bindTo(scope)

        val client =
            TiltakspengerApi(
                basePath = basePath,
                httpClient =
                    RestClient
                        .baseClient()
                        .newBuilder()
                        .addInterceptor(
                            HeadersInterceptor {
                                mapOf(
                                    "nav-call-id" to getCallId(),
                                )
                            },
                        ).addInterceptor(
                            tjenestekallLoggingInterceptorFactory("TiltaksPenger") { request ->
                                requireNotNull(request.header("nav-call-id")) {
                                    "Kall uten \"nav-call-id\" er ikke lov"
                                }
                            },
                        ).addInterceptor(
                            AuthorizationInterceptor {
                                tokenProvider.createMachineToMachineToken()
                            },
                        ).build(),
            )

        return TiltakspengerServiceImpl(client)
    }
}
