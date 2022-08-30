package no.nav.modiapersonoversikt.consumer.utbetaling

import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.apis.UtbetaldataV1Api
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class UtbetalingConfig {
    private val basePath = EnvironmentUtils.getRequiredProperty("REST_UTBETALING_V1_ENDPOINTURL")
    private val httpClient: OkHttpClient = RestClient.baseClient().newBuilder().build()

    @Bean
    open fun utbetalingV1() = UtbetaldataV1Api(
        basePath = basePath,
        httpClient = httpClient
    )
}
