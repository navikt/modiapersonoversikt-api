package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import no.nav.common.utils.EnvironmentUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class BrukernotifikasjonConfig {
    private val url: String = EnvironmentUtils.getRequiredProperty("DITTNAV_EVENTER_MODIA_URL")

    @Bean
    open fun brukernotifikasjonService(): Brukernotifikasjon.Service = BrukernotifikasjonService(
        BrukernotifikasjonClient(
            baseUrl = url
        )
    )
}
