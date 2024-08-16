package no.nav.modiapersonoversikt.service.enhetligkodeverk

import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.*
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.felleskodeverk.FellesKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave.OppgaveKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.sfhenvendelse.SfHenvendelseKodeverk
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class EnhetligKodeverkConfig {
    @Bean
    open fun enhetligKodeverk(
        machineToMachineTokenClient: MachineToMachineTokenClient,
        unleashService: UnleashService,
    ): EnhetligKodeverk.Service {
        return EnhetligKodeverkServiceImpl(
            KodeverkProviders(
                fellesKodeverk = FellesKodeverk.Provider(machineToMachineTokenClient, unleashService),
                sfHenvendelseKodeverk = SfHenvendelseKodeverk.Provider(machineToMachineTokenClient, unleashService),
                oppgaveKodeverk = OppgaveKodeverk.Provider(machineToMachineTokenClient, unleashService),
            ),
        )
    }
}
