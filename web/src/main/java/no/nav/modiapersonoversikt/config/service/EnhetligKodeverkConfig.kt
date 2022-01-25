package no.nav.modiapersonoversikt.config.service

import no.nav.common.sts.SystemUserTokenProvider
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverkServiceImpl
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.FellesKodeverkProvider
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.KodeverkProviders
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.OppgaveKodeverkProvider
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.SfHenvendelseKodeverkProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class EnhetligKodeverkConfig {
    @Bean
    open fun enhetligKodeverk(stsClient: SystemUserTokenProvider): EnhetligKodeverk.Service {
        return EnhetligKodeverkServiceImpl(
            KodeverkProviders(
                fellesKodeverk = FellesKodeverkProvider(FellesKodeverkProvider.createKodeverkApi()),
                sfHenvendelseKodeverk = SfHenvendelseKodeverkProvider(SfHenvendelseKodeverkProvider.createKodeverkApi(stsClient)),
                oppgaveKodeverk = OppgaveKodeverkProvider(OppgaveKodeverkProvider.createKodeverkApi())
            )
        )
    }
}
