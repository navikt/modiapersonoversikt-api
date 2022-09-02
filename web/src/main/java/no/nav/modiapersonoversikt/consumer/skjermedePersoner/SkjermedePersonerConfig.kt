package no.nav.modiapersonoversikt.consumer.skjermedePersoner

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SkjermedePersonerConfig {
    val scope = DownstreamApi(
        application = "skjermede-personer-pip",
        namespace = "nom",
        cluster = EnvironmentUtils.getRequiredProperty("GCP_CLUSTER")
    )
    val url = EnvironmentUtils.getRequiredProperty("SKJERMEDE_PERSONER_PIP_URL")

    @Autowired
    lateinit var tokenProvider: MachineToMachineTokenClient

    @Bean
    open fun skjermedePersoner(): SkjermedePersonerApi {
        val httpClient: OkHttpClient = RestClient.baseClient().newBuilder()
            .addInterceptor(XCorrelationIdInterceptor())
            .addInterceptor(
                LoggingInterceptor("SkjermedePersoner") { request ->
                    requireNotNull(request.header("X-Correlation-ID")) {
                        "Kall uten \"X-Correlation-ID\" er ikke lov"
                    }
                }
            )
            .addInterceptor(
                AuthorizationInterceptor {
                    tokenProvider.createMachineToMachineToken(scope)
                }
            )
            .build()
        return SkjermedePersonerApiImpl(url, httpClient)
    }
}
