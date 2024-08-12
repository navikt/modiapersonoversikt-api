package no.nav.modiapersonoversikt.consumer.utbetaling

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.apis.UtbetaldataV2Api
import no.nav.modiapersonoversikt.infrastructure.http.*
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class UtbetalingConfig {
    private val basePath = getRequiredProperty("REST_UTBETALING_ENDPOINTURL")
    private val scope = DownstreamApi.parse(getRequiredProperty("UTBETALING_SCOPE"))

    @Bean
    open fun utbetalingV2Api(tokenClient: MachineToMachineTokenClient) =
        UtbetaldataV2Api(
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
                        LoggingInterceptor("UtbetaldataV2", LoggingInterceptor.Config(ignoreResponseBody = false)) { request ->
                            requireNotNull(request.header("nav-call-id")) {
                                "Kall uten \"nav-call-id\" er ikke lov"
                            }
                        },
                    )
                    .addInterceptor(
                        AuthorizationInterceptor {
                            tokenClient.createMachineToMachineToken(scope)
                        },
                    )
                    .build(),
        )
}
