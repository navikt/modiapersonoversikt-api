package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.infrastructure.ping.ConsumerPingable
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.bindTo
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
open class ArbeidsrettetOppfolgingConfig {
    private val url: String = getRequiredProperty("VEILARBOPPFOLGINGAPI_URL")
    private val downstreamApi = DownstreamApi.parse(getRequiredProperty("VEILARBOPPFOLGINGAPI_SCOPE"))

    @Bean
    open fun oppfolgingsApi(
        ansattService: AnsattService,
        onBehalfOfTokenClient: OnBehalfOfTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): ArbeidsrettetOppfolging.Service {
        val httpClient =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(XCorrelationIdInterceptor())
                .addInterceptor(
                    tjenestekallLoggingInterceptorFactory("Oppfolging") {
                        requireNotNull(it.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                ).addInterceptor(
                    AuthorizationInterceptor {
                        AuthContextUtils.requireBoundedClientOboToken(onBehalfOfTokenClient.bindTo(downstreamApi))
                    },
                ).build()

        return ArbeidsrettetOppfolgingServiceImpl(
            apiUrl = url,
            ansattService = ansattService,
            httpClient = httpClient,
        )
    }

    @Bean
    open fun oppfolgingsApiPing(service: ArbeidsrettetOppfolging.Service): Pingable =
        ConsumerPingable(
            "OppfolgingsInfoApi",
            service::ping,
        )
}
