package no.nav.modiapersonoversikt.service.azure

import no.nav.common.client.msgraph.CachedMsGraphClient
import no.nav.common.client.msgraph.MsGraphClient
import no.nav.common.client.msgraph.MsGraphHttpClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.utils.bindTo
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
open class AzureADServiceConfig {
    @Bean
    open fun msGraphClient(): MsGraphClient = CachedMsGraphClient(MsGraphHttpClient(EnvironmentUtils.getRequiredProperty("MS_GRAPH_URL")))

    @Bean
    open fun azureADService(
        oboflowTokenProvider: OnBehalfOfTokenClient,
        msGraphClient: MsGraphClient,
    ): AzureADService =
        AzureADServiceImpl(
            tokenClient = oboflowTokenProvider.bindTo(EnvironmentUtils.getRequiredProperty("MS_GRAPH_SCOPE")),
            msGraphClient = msGraphClient,
        )
}
