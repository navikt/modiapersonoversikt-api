package no.nav.modiapersonoversikt.service.soknadsstatus

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.apis.SoknadsstatusControllerApi
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.HeadersInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.bindTo
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private val url = EnvironmentUtils.getRequiredProperty("MODIA_SOKNADSSTATUS_API_URL")
private val downstreamApi = DownstreamApi.parse(EnvironmentUtils.getRequiredProperty("MODIA_SOKNADSSTATUS_SCOPE"))

@Configuration
open class SoknadsstatusConfig {
    @Bean
    open fun soknadsstatusService(
        tokenClient: OnBehalfOfTokenClient,
        unleashService: UnleashService,
    ) = SoknadsstatusServiceImpl(tokenClient.bindTo(downstreamApi), unleashService)
}

object SoknadsstatusApiFactory {
    fun createClient(
        tokenProvider: () -> String,
        unleashService: UnleashService,
    ): OkHttpClient =
        RestClient.baseClient().newBuilder()
            .addInterceptor(
                HeadersInterceptor {
                    mapOf(
                        "nav-call-id" to getCallId(),
                    )
                },
            )
            .addInterceptor(
                LoggingInterceptor(unleashService, "ModiaSoknadsstatusV1") { request ->
                    requireNotNull(request.header("nav-call-id")) {
                        "Kall uten \"nav-call-id\" er ikke lov"
                    }
                },
            )
            .addInterceptor(
                AuthorizationInterceptor {
                    tokenProvider()
                },
            )
            .readTimeout(15.seconds.toJavaDuration())
            .build()

    fun createSoknadsstatusApi(
        oboClient: BoundedOnBehalfOfTokenClient,
        unleashService: UnleashService,
    ) = SoknadsstatusControllerApi(url, createClient(oboClient.asTokenProvider(), unleashService))

    private fun BoundedOnBehalfOfTokenClient.asTokenProvider(): () -> String =
        {
            AuthContextUtils.requireBoundedClientOboToken(this)
        }
}
