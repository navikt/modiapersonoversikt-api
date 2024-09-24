package no.nav.modiapersonoversikt.service.azure

import io.ktor.http.*
import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.bindTo
import no.nav.personoversikt.common.logging.TjenestekallLogger
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
open class AzureADServiceConfig {
    @Bean
    open fun azureADService(
        oboflowTokenProvider: OnBehalfOfTokenClient,
        unleashService: UnleashService,
        tjenestekallLogger: TjenestekallLogger,
    ): AzureADService {
        val httpClient =
            RestClient
                .baseClient()
                .newBuilder()
                .addInterceptor(
                    LoggingInterceptor(unleashService, "AzureAd", tjenestekallLogger) {
                        getCallId()
                    },
                ).build()

        return AzureADServiceImpl(
            tokenClient = oboflowTokenProvider.bindTo(EnvironmentUtils.getRequiredProperty("MS_GRAPH_SCOPE")),
            graphUrl = Url(EnvironmentUtils.getRequiredProperty("MS_GRAPH_URL")),
            httpClient = httpClient,
        )
    }
}
