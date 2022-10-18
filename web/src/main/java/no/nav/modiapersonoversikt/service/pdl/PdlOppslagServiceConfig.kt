package no.nav.modiapersonoversikt.service.pdl

import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.bindTo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PdlOppslagServiceConfig {
    @Bean
    open fun pdlOppslagService(
        stsService: SystemUserTokenProvider,
        machineToMachineTokenClient: MachineToMachineTokenClient,
        oboTokenClient: OnBehalfOfTokenClient
    ): PdlOppslagService = PdlOppslagServiceImpl(
        stsService,
        machineToMachineTokenClient.bindTo(downstreamApi),
        oboTokenClient.bindTo(downstreamApi)
    )

    companion object {
        val downstreamApi = DownstreamApi.parse(EnvironmentUtils.getRequiredProperty("PDL_SCOPE"))
    }
}
