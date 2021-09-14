package no.nav.modiapersonoversikt.config.service

import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverkServiceImpl
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkProviders
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseApiFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class EnhetligKodeverkConfig {
    @Bean
    open fun enhetligKodeverk(): EnhetligKodeverk.Service {
        return EnhetligKodeverkServiceImpl(
            KodeverkProviders(
                fellesKodeverk = KodeverkProviders.createFelleskodeverkApi(),
                sfHenvendelseKodeverk = SfHenvendelseApiFactory.createHenvendelseKodeverkApi()
            )
        )
    }
}
