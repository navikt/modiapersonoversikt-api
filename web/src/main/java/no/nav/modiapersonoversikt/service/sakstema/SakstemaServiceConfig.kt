package no.nav.modiapersonoversikt.service.sakstema

import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.saf.SafService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SakstemaServiceConfig {
    @Bean
    open fun sakstemaService(
        safService: SafService,
        kodeverk: EnhetligKodeverk.Service,
    ): SakstemaServiceImpl = SakstemaServiceImpl(safService, kodeverk)
}
