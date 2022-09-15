package no.nav.modiapersonoversikt.legacy.sak

import no.nav.modiapersonoversikt.consumer.sakogbehandling.SakOgBehandlingService
import no.nav.modiapersonoversikt.consumer.sakogbehandling.SakOgBehandlingServiceImpl
import no.nav.modiapersonoversikt.legacy.sak.service.SakstemaService
import no.nav.modiapersonoversikt.legacy.sak.service.TilgangskontrollServiceImpl
import no.nav.modiapersonoversikt.legacy.sak.service.interfaces.TilgangskontrollService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SakServiceConfig {
    @Bean
    open fun sakOgBehandlingService(porttype: SakOgBehandlingV1, pdl: PdlOppslagService): SakOgBehandlingService {
        return SakOgBehandlingServiceImpl(
            porttype,
            pdl
        )
    }

    @Bean
    open fun sakstemaService(): SakstemaService {
        return SakstemaService()
    }

    @Bean
    open fun tilgangskontrollService(): TilgangskontrollService? {
        return TilgangskontrollServiceImpl()
    }
}
