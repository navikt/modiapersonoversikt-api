package no.nav.modiapersonoversikt.service.arbeidsfordeling

import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.egenansatt.EgenAnsattService
import no.nav.modiapersonoversikt.rest.persondata.PersondataService
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ArbeidsfordelingConfig {
    @Bean
    open fun arbeidsfordelingService(
        norgApi: NorgApi,
        persondataService: PersondataService,
        egenAnsattService: EgenAnsattService,
        skjermedePersonerApi: SkjermedePersonerApi,
        unleashService: UnleashService
    ): ArbeidsfordelingService {
        return ArbeidsfordelingServiceImpl(
            norgApi,
            persondataService,
            egenAnsattService,
            skjermedePersonerApi,
            unleashService
        )
    }
}
