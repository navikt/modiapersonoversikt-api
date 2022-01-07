package no.nav.modiapersonoversikt.service.arbeidsfordeling

import no.nav.modiapersonoversikt.config.endpoint.kodeverksmapper.Kodeverksmapper
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.egenansatt.EgenAnsattService
import no.nav.modiapersonoversikt.rest.persondata.PersondataService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ArbeidsfordelingConfig {
    @Bean
    open fun arbeidsfordelingService(
        norgApi: NorgApi,
        persondataService: PersondataService,
        kodeverksmapper: Kodeverksmapper,
        egenAnsattService: EgenAnsattService
    ): ArbeidsfordelingService {
        return ArbeidsfordelingServiceImpl(
            norgApi,
            persondataService,
            kodeverksmapper,
            egenAnsattService
        )
    }
}
