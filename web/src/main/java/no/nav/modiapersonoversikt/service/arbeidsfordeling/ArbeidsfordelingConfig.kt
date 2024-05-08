package no.nav.modiapersonoversikt.service.arbeidsfordeling

import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.pdlPip.PdlPipApi
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ArbeidsfordelingConfig {
    @Bean
    open fun arbeidsfordelingService(
        norgApi: NorgApi,
        pdlOppslagService: PdlOppslagService,
        pdlPipApi: PdlPipApi,
        skjermedePersonerApi: SkjermedePersonerApi,
    ): ArbeidsfordelingService {
        return ArbeidsfordelingServiceImpl(
            norgApi,
            pdlPipApi,
            skjermedePersonerApi,
        )
    }
}
