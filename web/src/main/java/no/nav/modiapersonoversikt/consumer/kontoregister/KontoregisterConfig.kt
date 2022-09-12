package no.nav.modiapersonoversikt.consumer.kontoregister

import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.config.InDevCondition
import no.nav.modiapersonoversikt.consumer.kontoregister.generated.apis.KontoregisterV1Api
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.types.Pingable
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration

@Configuration
open class KontoregisterConfig {
    val scope = DownstreamApi(
        application = "sokos-kontoregister-person",
        namespace = "okonomi",
        cluster = EnvironmentUtils.getRequiredProperty("GCP_CLUSTER")
    )
    val url = EnvironmentUtils.getRequiredProperty("KONTOREGISTER_REST_URL")
    val isProd = EnvironmentUtils.isProduction()

    @Bean
    open fun kontoregisterApi(tokenClient: MachineToMachineTokenClient) = KontoregisterV1Api(
        basePath = url,
        httpClient = RestClient.baseClient().newBuilder()
            .addInterceptor(
                LoggingInterceptor("Kontoregister") { request ->
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

    @Bean
    @Conditional(InDevCondition::class)
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
