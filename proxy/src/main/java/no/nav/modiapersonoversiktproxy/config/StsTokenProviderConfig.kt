package no.nav.modiapersonoversiktproxy.config

import io.prometheus.metrics.model.registry.PrometheusRegistry
import no.nav.common.cxf.StsConfig
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversiktproxy.utils.AppConstants
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class StsTokenProviderConfig {
    @Bean
    open fun stsConfig(): StsConfig =
        StsConfig
            .builder()
            .url(EnvironmentUtils.getRequiredProperty("SECURITYTOKENSERVICE_URL"))
            .username(AppConstants.SYSTEMUSER_USERNAME)
            .password(AppConstants.SYSTEMUSER_PASSWORD)
            .build()

    @Bean
    open fun collectorRegistry(): PrometheusRegistry = PrometheusRegistry()
}
