package no.nav.modiapersonoversikt.service.sakstema

import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.saf.SafService
import no.nav.modiapersonoversikt.service.sakogbehandling.SakOgBehandlingService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SakstemaServiceConfig {
    @Bean
    open fun sakstemaService(
        safService: SafService,
        sakOgBehandlingService: SakOgBehandlingService,
        kodeverk: EnhetligKodeverk.Service
    ): SakstemaService {
        return SakstemaService(
            safService,
            sakOgBehandlingService,
            kodeverk
        )
    }
}
