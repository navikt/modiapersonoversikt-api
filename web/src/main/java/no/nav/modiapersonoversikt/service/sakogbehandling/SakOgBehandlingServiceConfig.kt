package no.nav.modiapersonoversikt.service.sakogbehandling

import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
open class SakOgBehandlingServiceConfig {
    @Bean
    open fun sakOgBehandlingService(
        sakOgBehandlingV1: SakOgBehandlingV1,
        pdl: PdlOppslagService,
    ) = SakOgBehandlingServiceImpl(sakOgBehandlingV1, pdl)
}
