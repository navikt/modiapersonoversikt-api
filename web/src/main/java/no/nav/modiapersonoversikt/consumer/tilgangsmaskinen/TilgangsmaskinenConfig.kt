package no.nav.modiapersonoversikt.consumer.tilgangsmaskinen

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.bindTo
import no.nav.personoversikt.common.logging.TjenestekallLogger
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TilgangsmaskinenConfig {
    private val scope = DownstreamApi.parse(getRequiredProperty("TILGANGSMASKINEN_SCOPE"))
    private val url: String = getRequiredProperty("TILGANGSMASKINEN_URL")

    @Bean
    open fun tilgangsmaskinen(
        tokenProvider: OnBehalfOfTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
        tjenestekallLogger: TjenestekallLogger,
        objectMapper: ObjectMapper,
    ): Tilgangsmaskinen {
        val oboTokenProvider = tokenProvider.bindTo(scope)

        val httpClient: OkHttpClient =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(
                    tjenestekallLoggingInterceptorFactory("TilgangsMaskinen") { request ->
                        request.header("Nav-Call-Id") ?: "-"
                    },
                ).addInterceptor(
                    AuthorizationInterceptor {
                        AuthContextUtils.requireBoundedClientOboToken(oboTokenProvider)
                    },
                ).build()
        return TilgangsmaskinenImpl(url, httpClient, tjenestekallLogger, objectMapper)
    }
}
