package no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
open class ArenaInfotrygdApiConfig {
    private val scope = DownstreamApi.parse(getRequiredProperty("MODIAPERSONOVERSIKT_API_PROXY_SCOPE"))
    private val url: String = getRequiredProperty("MODIAPERSONOVERSIKT_API_PROXY_URL")

    @Autowired
    lateinit var tokenProvider: MachineToMachineTokenClient

    @Bean
    open fun arenaInfotrygdApi(): ArenaInfotrygdApi {
        val httpClient: OkHttpClient =
            RestClient.baseClient().newBuilder()
                .addInterceptor(XCorrelationIdInterceptor())
                .connectTimeout(30L, TimeUnit.SECONDS)
                .addInterceptor(
                    LoggingInterceptor("SkjermedePersoner") { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                )
                .addInterceptor(
                    AuthorizationInterceptor {
                        tokenProvider.createMachineToMachineToken(scope)
                    },
                )
                .build()
        return ArenaInfotrygdApiImpl(url, httpClient)
    }
}
