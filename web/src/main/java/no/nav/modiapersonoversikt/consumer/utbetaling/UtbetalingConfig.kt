package no.nav.modiapersonoversikt.consumer.utbetaling

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.apis.UtbetaldataV1Api
import no.nav.modiapersonoversikt.infrastructure.http.*
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class UtbetalingConfig {
    private val basePath = EnvironmentUtils.getRequiredProperty("REST_UTBETALING_V1_ENDPOINTURL")
    private val scope = DownstreamApi(
        application = "sokos-utbetaldata",
        namespace = "okonomi",
        cluster = EnvironmentUtils.getRequiredProperty("ONPREM_CLUSTER_NAME")
    )

    @Bean
    open fun utbetalingV1Api(tokenClient: MachineToMachineTokenClient) = UtbetaldataV1Api(
        basePath = basePath,
        httpClient = RestClient.baseClient().newBuilder()
            .addInterceptor(
                HeadersInterceptor {
                    mapOf(
                        "nav-call-id" to getCallId()
                    )
                }
            )
            .addInterceptor(
                LoggingInterceptor("UtbetaldataV1") { request ->
                    requireNotNull(request.header("nav-call-id")) {
                        "Kall uten \"nav-call-id\" er ikke lov"
                    }
                }
            )
            .addInterceptor(
                AuthorizationInterceptor {
                    tokenClient.createMachineToMachineToken(scope)
                }
            )
            .build()
    )
}
