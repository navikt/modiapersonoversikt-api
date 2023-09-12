package no.nav.modiapersonoversikt.service.soknadsstatus

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.apis.SoknadsstatusControllerApi
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.HeadersInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
@Configuration
open class SoknadsstatusConfig {

    private val basePath = EnvironmentUtils.getRequiredProperty("MODIA_SOKNADSSTATUS_API_URL")
    private val scope = DownstreamApi.parse(EnvironmentUtils.getRequiredProperty("MODIA_SOKNADSSTATUS_SCOPE"))

    @Bean
    open fun soknadsstatusApi(tokenClient: MachineToMachineTokenClient) = SoknadsstatusControllerApi(
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
                LoggingInterceptor("ModiaSoknadsstatusV1") { request ->
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