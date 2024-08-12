package no.nav.modiapersonoversikt.service.oppgavebehandling

import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.bindTo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OppgaveBehandlingServiceConfig {
    @Bean
    open fun oppgaveBehandlingService(
        pdlOppslagService: PdlOppslagService,
        ansattService: AnsattService,
        tilgangskontroll: Tilgangskontroll,
        oboTokenClient: OnBehalfOfTokenClient,
        machineToMachineTokenClient: MachineToMachineTokenClient,
        unleashService: UnleashService,
    ): OppgaveBehandlingService {
        return RestOppgaveBehandlingServiceImpl(
            pdlOppslagService = pdlOppslagService,
            ansattService = ansattService,
            tilgangskontroll = tilgangskontroll,
            oboTokenClient = oboTokenClient.bindTo(OppgaveApiFactory.downstreamApi),
            unleashService = unleashService,
            machineToMachineTokenClient = machineToMachineTokenClient.bindTo(OppgaveApiFactory.downstreamApi),
        )
    }
}
