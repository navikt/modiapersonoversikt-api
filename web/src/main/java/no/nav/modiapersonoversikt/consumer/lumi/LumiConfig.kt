package no.nav.modiapersonoversikt.consumer.lumi

import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.utils.bindTo
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!local")
open class LumiConfig {
    private val baseUrl: String = getRequiredProperty("LUMI_API_HOST")
    private val audience: String = getRequiredProperty("LUMI_AUDIENCE")

    @Bean
    open fun lumiService(
        okHttpClient: OkHttpClient,
        onBehalfOfTokenClient: OnBehalfOfTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): LumiService {
        val oboTokenProvider = onBehalfOfTokenClient.bindTo(audience)
        val httpClient =
            okHttpClient
                .newBuilder()
                .addInterceptor(XCorrelationIdInterceptor())
                .addInterceptor(
                    tjenestekallLoggingInterceptorFactory("Lumi") {
                        requireNotNull(it.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                ).addInterceptor(
                    AuthorizationInterceptor {
                        AuthContextUtils.requireBoundedClientOboToken(oboTokenProvider)
                    },
                ).build()

        return LumiServiceImpl(baseUrl, httpClient)
    }
}
