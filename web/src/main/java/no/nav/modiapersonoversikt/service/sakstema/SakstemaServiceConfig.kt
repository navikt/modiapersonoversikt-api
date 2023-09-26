package no.nav.modiapersonoversikt.service.sakstema

import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.saf.SafService
import no.nav.modiapersonoversikt.service.sakogbehandling.SakOgBehandlingService
import no.nav.modiapersonoversikt.service.soknadsstatus.SoknadsstatusService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SakstemaServiceConfig {
    @Bean
    open fun sakstemaService(
        safService: SafService,
        sakOgBehandlingService: SakOgBehandlingService,
        kodeverk: EnhetligKodeverk.Service,
        soknadsstatusService: SoknadsstatusService
    ): SakstemaServiceImpl {
        return SakstemaServiceImpl(safService, sakOgBehandlingService, kodeverk, soknadsstatusService)
    }
}
