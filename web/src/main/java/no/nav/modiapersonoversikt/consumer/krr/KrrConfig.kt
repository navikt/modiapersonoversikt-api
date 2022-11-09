package no.nav.modiapersonoversikt.consumer.krr

import KrrServiceImpl
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
open class KrrConfig {
    @Bean
    @Primary
    open fun krrService(machineToMachineTokenClient: MachineToMachineTokenClient): Krr.Service {
        return KrrServiceImpl(
            EnvironmentUtils.getRequiredProperty("KRR_REST_URL"),
            machineToMachineTokenClient
        )
    }
}
