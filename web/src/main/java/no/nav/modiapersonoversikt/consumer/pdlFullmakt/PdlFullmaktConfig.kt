package no.nav.modiapersonoversikt.consumer.pdlFullmaktApi

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.bindTo
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PdlFullmaktConfig {
    private val scope = DownstreamApi.parse(getRequiredProperty("PDL_FULLMAKT_SCOPE"))
    private val url: String = getRequiredProperty("PDL_FULLMAKT_URL")

    @Autowired
    lateinit var tokenProvider: OnBehalfOfTokenClient

    @Bean
    open fun pdlPip(): PdlFullmaktApi {
        val oboTokenProvider = tokenProvider.bindTo(scope)

        val httpClient: OkHttpClient =
            RestClient.baseClient().newBuilder()
                .addInterceptor(XCorrelationIdInterceptor())
                .addInterceptor(
                    LoggingInterceptor("PdlFullmaktApi") { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                )
                .addInterceptor(
                    AuthorizationInterceptor {
                        AuthContextUtils.requireBoundedClientOboToken(oboTokenProvider)
                    },
                )
                .build()

        return PdlFullmaktApiImpl(url, httpClient)
    }
}
