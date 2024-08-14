package no.nav.modiapersonoversikt.consumer.tiltakspenger

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.consumer.tiltakspenger.generated.apis.TiltakspengerApi
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.*
import no.nav.modiapersonoversikt.service.unleash.UnleashService
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
        onBehalfOfTokenClient: OnBehalfOfTokenClient,
        unleashService: UnleashService,
    ): TiltakspengerService {
        val oboTokenProvider = onBehalfOfTokenClient.bindTo(scope)

        val client =
            TiltakspengerApi(
                basePath = basePath,
                httpClient =
                    RestClient.baseClient().newBuilder()
                        .addInterceptor(
                            HeadersInterceptor {
                                mapOf(
                                    "nav-call-id" to getCallId(),
                                )
                            },
                        )
                        .addInterceptor(
                            LoggingInterceptor(unleashService, "TiltaksPenger") { request ->
                                requireNotNull(request.header("nav-call-id")) {
                                    "Kall uten \"nav-call-id\" er ikke lov"
                                }
                            },
                        )
                        .addInterceptor(
                            AuthorizationInterceptor {
                                AuthContextUtils.requireBoundedClientOboToken(oboTokenProvider)
                            },
                        )
                        .build(),
            )

        return TiltakspengerServiceImpl(client)
    }
}
