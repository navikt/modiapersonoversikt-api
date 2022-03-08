package no.nav.modiapersonoversikt.consumer.skjermedePersoner

import no.nav.common.rest.client.RestClient
import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SkjermedePersonerConfig {
    var url = EnvironmentUtils.getRequiredProperty("NOM_URL")

    @Autowired
    lateinit var tokenProvider: ServiceToServiceTokenProvider

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
            .addInterceptor(AuthorizationInterceptor {
                tokenProvider.getServiceToken(
                    "skjermede-personer-pip",
                    "nom",
                    EnvironmentUtils
                        .getRequiredProperty("SKJERMEDE_PERSONER_PIP_CLUSTER")
                )
            }
        )
            .build()
        return SkjermedePersonerApiImpl(url, httpClient)
    }
}