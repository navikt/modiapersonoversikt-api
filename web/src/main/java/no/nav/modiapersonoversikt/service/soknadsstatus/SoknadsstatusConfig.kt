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
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.bindTo
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private val url = EnvironmentUtils.getRequiredProperty("")
private val downstreamApi = DownstreamApi.parse(EnvironmentUtils.getRequiredProperty(""))

@Configuration
open class SoknadsstatusConfig {
    @Bean
    open fun soknadsstatusApi(tokenClient: OnBehalfOfTokenClient) = SoknadsstatusApiFactory.createSoknadsstatusApi(tokenClient.bindTo(downstreamApi))
}

object SoknadsstatusApiFactory {
    fun createClient(tokenProvider: () -> String): OkHttpClient = RestClient.baseClient().newBuilder()
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
                tokenProvider()
            }
        )
        .readTimeout(15.seconds.toJavaDuration())
        .build()

    fun createSoknadsstatusApi(oboClient: BoundedOnBehalfOfTokenClient) =
        SoknadsstatusControllerApi(url, createClient(oboClient.asTokenProvider()))

    private fun BoundedOnBehalfOfTokenClient.asTokenProvider(): () -> String = {
        AuthContextUtils.requireBoundedClientOboToken(this)
    }
}
