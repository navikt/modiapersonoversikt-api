package no.nav.modiapersonoversikt.service.varsel

import no.nav.modiapersonoversikt.consumer.brukernotifikasjon.Brukernotifikasjon
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.tjeneste.virksomhet.brukervarsel.v1.BrukervarselV1
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
open class VarslerServiceConfig {

    @Autowired
    private lateinit var unleashService: UnleashService

    @Bean
    open fun varslerService(
        brukervarselV1: BrukervarselV1,
        brukernotifikasjonService: Brukernotifikasjon.Service
    ): VarslerService = VarslerServiceImpl(
        brukervarselV1,
        brukernotifikasjonService,
        unleashService
    )
}
