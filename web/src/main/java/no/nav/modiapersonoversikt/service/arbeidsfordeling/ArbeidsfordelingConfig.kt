package no.nav.modiapersonoversikt.service.arbeidsfordeling

import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.rest.persondata.PersondataService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ArbeidsfordelingConfig {
    @Bean
    open fun arbeidsfordelingService(
        norgApi: NorgApi,
        persondataService: PersondataService,
        skjermedePersonerApi: SkjermedePersonerApi
    ): ArbeidsfordelingService {
        return ArbeidsfordelingServiceImpl(
            norgApi,
            persondataService,
            skjermedePersonerApi
        )
    }
}
