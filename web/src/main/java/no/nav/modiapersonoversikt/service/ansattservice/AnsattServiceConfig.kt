package no.nav.modiapersonoversikt.service.ansattservice

import no.nav.common.client.nom.NomClient
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.service.azure.AzureADService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AnsattServiceConfig {
    @Bean
    open fun ansattService(
        norgApi: NorgApi,
        nomClient: NomClient,
        azureADService: AzureADService,
    ): AnsattService = AnsattServiceImpl(norgApi, nomClient, azureADService)
}
