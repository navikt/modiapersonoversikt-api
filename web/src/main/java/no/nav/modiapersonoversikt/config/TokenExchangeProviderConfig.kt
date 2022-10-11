package no.nav.modiapersonoversikt.config

import no.nav.common.cxf.StsConfig
import no.nav.common.sts.NaisSystemUserTokenProvider
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.token_client.builder.AzureAdTokenClientBuilder
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.common.utils.EnvironmentUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TokenExchangeProviderConfig {
    companion object {
        const val SOAP_STS_URL_PROPERTY = "SECURITYTOKENSERVICE_URL"
        const val REST_STS_URL_PROPERTY = "SECURITY_TOKEN_SERVICE_DISCOVERY_URL"
    }

    @Bean
    open fun stsConfig(): StsConfig = StsConfig
        .builder()
        .url(EnvironmentUtils.getRequiredProperty(SOAP_STS_URL_PROPERTY))
        .username(AppConstants.SYSTEMUSER_USERNAME)
        .password(AppConstants.SYSTEMUSER_PASSWORD)
        .build()

    @Bean
    open fun systemUserTokenProvider(): SystemUserTokenProvider = NaisSystemUserTokenProvider(
        EnvironmentUtils.getRequiredProperty(REST_STS_URL_PROPERTY),
        AppConstants.SYSTEMUSER_USERNAME,
        AppConstants.SYSTEMUSER_PASSWORD
    )

    @Bean
    open fun machineToMachineTokenProvider(): MachineToMachineTokenClient = AzureAdTokenClientBuilder
        .builder()
        .withNaisDefaults()
        .buildMachineToMachineTokenClient()

    @Bean
    open fun oboflowTokenProvider(): OnBehalfOfTokenClient = AzureAdTokenClientBuilder
        .builder()
        .withNaisDefaults()
        .buildOnBehalfOfTokenClient()
}
