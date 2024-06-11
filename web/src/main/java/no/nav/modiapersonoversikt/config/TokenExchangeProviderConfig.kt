package no.nav.modiapersonoversikt.config

import io.ktor.http.*
import no.nav.common.token_client.builder.AzureAdTokenClientBuilder
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.service.azure.AzureADServiceImpl
import no.nav.modiapersonoversikt.utils.bindTo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TokenExchangeProviderConfig {
    @Bean
    open fun machineToMachineTokenProvider(): MachineToMachineTokenClient =
        AzureAdTokenClientBuilder
            .builder()
            .withNaisDefaults()
            .buildMachineToMachineTokenClient()

    @Bean
    open fun oboflowTokenProvider(): OnBehalfOfTokenClient =
        AzureAdTokenClientBuilder
            .builder()
            .withNaisDefaults()
            .buildOnBehalfOfTokenClient()

    @Bean
    open fun azureADService(oboflowTokenProvider: OnBehalfOfTokenClient) =
        AzureADServiceImpl(
            graphUrl = Url(EnvironmentUtils.getRequiredProperty("MS_GRAPH_URL")),
            tokenClient = oboflowTokenProvider.bindTo(EnvironmentUtils.getRequiredProperty("MS_GRAPH_SCOPE")),
        )
}
