package no.nav.modiapersonoversikt.service.saf

import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.bindTo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SafConfig {
    val downstreamapi = DownstreamApi.parse(getRequiredProperty("SAF_SCOPE"))

    @Bean
    open fun safService(oboTokenClient: OnBehalfOfTokenClient): SafService =
        SafServiceImpl(
            oboTokenClient.bindTo(downstreamapi),
        )
}
