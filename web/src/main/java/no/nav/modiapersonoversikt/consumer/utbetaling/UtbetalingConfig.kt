package no.nav.modiapersonoversikt.consumer.utbetaling

import no.nav.common.rest.client.RestClient
import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.apis.UtbetaldataV1Api
import no.nav.modiapersonoversikt.infrastructure.http.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class UtbetalingConfig {
    private val basePath = EnvironmentUtils.getRequiredProperty("REST_UTBETALING_V1_ENDPOINTURL")
    private val cluster = EnvironmentUtils.getRequiredProperty("ONPREM_CLUSTER_NAME")

    @Bean
    open fun utbetalingV1(tokenClient: ServiceToServiceTokenProvider) = UtbetaldataV1Api(
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
                    tokenClient.getServiceToken("sokos-utbetaldata", "okonomi", cluster)
                }
            )
            .build()
    )
}
