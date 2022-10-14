package no.nav.modiapersonoversikt.service.pdl

import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.bindTo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PdlOppslagServiceConfig {
    @Bean
    open fun pdlOppslagService(machineToMachineTokenClient: MachineToMachineTokenClient): PdlOppslagService = PdlOppslagServiceImpl(
        machineToMachineTokenClient.bindTo(downstreamApi)
    )

    companion object {
        val downstreamApi = DownstreamApi.parse(EnvironmentUtils.getRequiredProperty("PDL_SCOPE"))
    }
}
