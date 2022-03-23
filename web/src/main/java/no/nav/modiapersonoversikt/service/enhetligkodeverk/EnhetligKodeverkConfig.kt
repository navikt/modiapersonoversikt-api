package no.nav.modiapersonoversikt.service.enhetligkodeverk

import no.nav.common.sts.SystemUserTokenProvider
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.*
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.felleskodeverk.FellesKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave.OppgaveKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.sfhenvendelse.SfHenvendelseKodeverk
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class EnhetligKodeverkConfig {
    @Bean
    open fun enhetligKodeverk(stsClient: SystemUserTokenProvider): EnhetligKodeverk.Service {
        return EnhetligKodeverkServiceImpl(
            KodeverkProviders(
                fellesKodeverk = FellesKodeverk.Provider(),
                sfHenvendelseKodeverk = SfHenvendelseKodeverk.Provider(stsClient),
                oppgaveKodeverk = OppgaveKodeverk.Provider(stsClient)
            )
        )
    }
}
