package no.nav.modiapersonoversikt.service.varsel

import no.nav.modiapersonoversikt.consumer.brukernotifikasjon.Brukernotifikasjon
import no.nav.tjeneste.virksomhet.brukervarsel.v1.BrukervarselV1
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
open class VarslerServiceConfig {

    @Bean
    open fun varslerService(
        brukervarselV1: BrukervarselV1,
        brukernotifikasjonService: Brukernotifikasjon.Service
    ): VarslerService = VarslerServiceImpl(
        brukervarselV1,
        brukernotifikasjonService
    )
}
