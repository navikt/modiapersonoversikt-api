package no.nav.modiapersonoversikt.service.oppgavebehandling

import no.nav.common.sts.SystemUserTokenProvider
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OppgaveBehandlingServiceConfig {
    @Bean
    open fun oppgaveBehandlingService(
        pdlOppslagService: PdlOppslagService,
        ansattService: AnsattService,
        tilgangskontroll: Tilgangskontroll,
        systemUserTokenProvider: SystemUserTokenProvider
    ): OppgaveBehandlingService {
        return RestOppgaveBehandlingServiceImpl(
            pdlOppslagService = pdlOppslagService,
            ansattService = ansattService,
            tilgangskontroll = tilgangskontroll,
            stsService = systemUserTokenProvider
        )
    }
}
