package no.nav.modiapersonoversikt.service.enhetligkodeverk

import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.*
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.felleskodeverk.FellesKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave.OppgaveKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.sfhenvendelse.SfHenvendelseKodeverk
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class EnhetligKodeverkConfig {
    @Bean
    open fun enhetligKodeverk(
        machineToMachineTokenClient: MachineToMachineTokenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): EnhetligKodeverk.Service =
        EnhetligKodeverkServiceImpl(
            KodeverkProviders(
                fellesKodeverk =
                    FellesKodeverk.Provider(
                        machineToMachineTokenClient,
                        tjenestekallLoggingInterceptorFactory,
                    ),
                sfHenvendelseKodeverk =
                    SfHenvendelseKodeverk.Provider(
                        machineToMachineTokenClient,
                        tjenestekallLoggingInterceptorFactory,
                    ),
                oppgaveKodeverk =
                    OppgaveKodeverk.Provider(
                        machineToMachineTokenClient,
                        tjenestekallLoggingInterceptorFactory,
                    ),
            ),
        )
}
