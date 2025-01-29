package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.apis.KodeverkFor14AVedtakApi
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.apis.Siste14AVedtakV2Api
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
import org.springframework.context.annotation.Profile

@Configuration
@EnableCaching
@Profile("!local")
open class VeilarbvedtaksstotteConfig {
    private val url: String = getRequiredProperty("VEILARBVEDTAKSTOTTEAPI_URL")
    private val downstreamApi = DownstreamApi.parse(getRequiredProperty("VEILARBVEDTAKSTOTTEAPI_SCOPE"))

    @Bean
    open fun veilarbvedtaksstotteService(
        ansattService: AnsattService,
        onBehalfOfTokenClient: OnBehalfOfTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): VeilarbvedtaksstotteService {
        val httpClient =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(XCorrelationIdInterceptor())
                .addInterceptor(
                    tjenestekallLoggingInterceptorFactory("Veilarbvedtaksstotte") {
                        requireNotNull(it.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                ).addInterceptor(
                    AuthorizationInterceptor {
                        AuthContextUtils.requireBoundedClientOboToken(onBehalfOfTokenClient.bindTo(downstreamApi))
                    },
                ).build()

        val siste14AVedtakApi = Siste14AVedtakV2Api(url, httpClient)
        val kodeverkFor14AVedtakApi = KodeverkFor14AVedtakApi(url, httpClient)

        return VeilarbvedtaksstotteServiceImpl(siste14AVedtakApi, kodeverkFor14AVedtakApi)
    }

    @Bean
    open fun veilarbvedtaksstotteApiPing(veilarbvedtaksstotteService: VeilarbvedtaksstotteService): Pingable =
        ConsumerPingable(
            "VeilarbvedtaksstotteApi",
            veilarbvedtaksstotteService::ping,
        )
}
