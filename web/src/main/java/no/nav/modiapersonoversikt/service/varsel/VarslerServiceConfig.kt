package no.nav.modiapersonoversikt.service.varsel

import no.nav.modiapersonoversikt.consumer.brukernotifikasjon.Brukernotifikasjon
import no.nav.personoversikt.common.logging.TjenestekallLogger
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
open class VarslerServiceConfig {
    @Bean
    open fun varslerService(
        brukernotifikasjonService: Brukernotifikasjon.Service,
        tjenestekallLogger: TjenestekallLogger,
    ): VarslerService = VarslerServiceImpl(brukernotifikasjonService, tjenestekallLogger)
}
