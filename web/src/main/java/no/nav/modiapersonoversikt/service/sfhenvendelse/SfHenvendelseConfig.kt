package no.nav.modiapersonoversikt.service.sfhenvendelse

import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.ping.ConsumerPingable
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseApiFactory.asTokenProvider
import no.nav.modiapersonoversikt.utils.bindTo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Configuration
open class SfHenvendelseConfig {
    @Bean
    open fun sfHenvendelseApi(
        pdlOppslagService: PdlOppslagService,
        norgApi: NorgApi,
        ansattService: AnsattService,
        oboTokenClient: OnBehalfOfTokenClient,
        machineToMachineTokenClient: MachineToMachineTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): SfHenvendelseService {
        val oboTokenClient = oboTokenClient.bindTo(SfHenvendelseApiFactory.downstreamApi())
        val httpClient =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(
                    tjenestekallLoggingInterceptorFactory("SF-Henvendelse") { request ->
                        requireNotNull(request.header("X-Correlation-ID")) {
                            "Kall uten \"X-Correlation-ID\" er ikke lov"
                        }
                    },
                ).addInterceptor(
                    AuthorizationInterceptor(oboTokenClient.asTokenProvider()),
                ).readTimeout(15.seconds.toJavaDuration())
                .build()

        return SfHenvendelseServiceImpl(
            pdlOppslagService = pdlOppslagService,
            norgApi = norgApi,
            ansattService = ansattService,
            httpClient = httpClient,
        )
    }

    @Bean
    open fun sfHenvendelseApiPing(service: SfHenvendelseService): ConsumerPingable =
        ConsumerPingable(
            "Salesforce - Henvendelse",
            service::ping,
        )
}
