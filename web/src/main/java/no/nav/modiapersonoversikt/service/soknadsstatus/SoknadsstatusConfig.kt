package no.nav.modiapersonoversikt.service.soknadsstatus

import no.nav.common.token_client.client.OnBehalfOfTokenClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SoknadsstatusConfig {
    @Bean
    open fun soknadsstatusApi(oboTokenClient: OnBehalfOfTokenClient) = SoknadsstatusApiImpl(oboTokenClient = oboTokenClient)

    @Bean
    open fun soknadsstatusApiPing(): Unit
}