package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.apis.Gjeldende14AVedtakApi
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.apis.KodeverkFor14AVedtakApi
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.infrastructure.ping.ConsumerPingable
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.bindTo
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
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
    open fun gjeldende14AVedtakApi(
        onBehalfOfTokenClient: OnBehalfOfTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): Gjeldende14AVedtakApi {
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

        return Gjeldende14AVedtakApi(url, httpClient)
    }

    @Bean
    open fun kodeverkFor14AVedtakApi(
        machineToMachineTokenClient: MachineToMachineTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): KodeverkFor14AVedtakApi {
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
                        machineToMachineTokenClient.createMachineToMachineToken(downstreamApi)
                    },
                ).build()

        return KodeverkFor14AVedtakApi(url, httpClient)
    }

    @Bean
    open fun veilarbvedtaksstotteService(
        gjeldende14AVedtakApi: Gjeldende14AVedtakApi,
        kodeverkFor14AVedtakApi: KodeverkFor14AVedtakApi,
    ): VeilarbvedtaksstotteService = VeilarbvedtaksstotteServiceImpl(gjeldende14AVedtakApi, kodeverkFor14AVedtakApi)

    @Bean
    open fun veilarbvedtaksstotteApiPing(veilarbvedtaksstotteService: VeilarbvedtaksstotteService): Pingable =
        ConsumerPingable(
            "VeilarbvedtaksstotteApi",
            veilarbvedtaksstotteService::ping,
        )
}
