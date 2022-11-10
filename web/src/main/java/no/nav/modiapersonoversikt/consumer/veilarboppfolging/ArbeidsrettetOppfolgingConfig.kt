package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.infrastructure.ping.ConsumerPingable
import no.nav.modiapersonoversikt.infrastructure.types.Pingable
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
        onBehalfOfTokenClient: OnBehalfOfTokenClient
    ): ArbeidsrettetOppfolging.Service {
        return ArbeidsrettetOppfolgingServiceImpl(
            apiUrl = url,
            ansattService = ansattService,
            oboTokenProvider = onBehalfOfTokenClient.bindTo(downstreamApi)

        )
    }

    @Bean
    open fun oppfolgingsApiPing(
        service: ArbeidsrettetOppfolging.Service
    ): Pingable {
        return ConsumerPingable(
            "OppfolgingsInfoApi",
            service::ping
        )
    }
}
