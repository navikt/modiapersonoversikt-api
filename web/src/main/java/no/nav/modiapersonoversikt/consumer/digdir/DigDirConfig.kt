package no.nav.modiapersonoversikt.consumer.digdir

import DigDirServiceImpl
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
open class DigDirConfig {
    @Bean
    @Primary
    open fun digDirService(machineToMachineTokenClient: MachineToMachineTokenClient): DigDir.Service {
        return DigDirServiceImpl(
            EnvironmentUtils.getRequiredProperty("DIG_DIR_REST_URL"),
            machineToMachineTokenClient
        )
    }
}
