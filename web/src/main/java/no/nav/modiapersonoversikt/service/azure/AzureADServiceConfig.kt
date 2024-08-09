package no.nav.modiapersonoversikt.service.azure

import io.ktor.http.*
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
    open fun azureADService(oboflowTokenProvider: OnBehalfOfTokenClient) =
        AzureADServiceImpl(
            tokenClient = oboflowTokenProvider.bindTo(EnvironmentUtils.getRequiredProperty("MS_GRAPH_SCOPE")),
            graphUrl = Url(EnvironmentUtils.getRequiredProperty("MS_GRAPH_URL")),
        )
}
