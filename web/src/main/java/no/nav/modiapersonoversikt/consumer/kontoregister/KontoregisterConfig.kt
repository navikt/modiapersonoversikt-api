package no.nav.modiapersonoversikt.consumer.kontoregister

import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.api.domain.kontoregister.generated.apis.KontoregisterV1Api
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.types.Pingable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class KontoregisterConfig {
    @Bean
    open fun kontoregisterApi() = KontoregisterV1Api(
        basePath = EnvironmentUtils.getRequiredProperty("KONTOREGISTER_REST_URL"),
        httpClient = RestClient.baseClient().newBuilder()
            .addInterceptor(
                LoggingInterceptor("Kontoregister") { request ->
                    requireNotNull(request.header("nav-call-id")) {
                        "Kall uten \"nav-call-id\" er ikke lov"
                    }
                }
            )
            .build()
    )

    @Bean
    open fun kontoregisterPing(client: KontoregisterV1Api): Pingable {
        return Pingable {
            SelfTestCheck("Kontoregister - V1", false) {
                try {
                    client.hentValutakoder()
                    HealthCheckResult.healthy()
                } catch (e: Throwable) {
                    HealthCheckResult.unhealthy(e)
                }
            }
        }
    }
}
