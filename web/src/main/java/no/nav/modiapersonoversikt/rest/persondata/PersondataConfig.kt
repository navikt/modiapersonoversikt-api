package no.nav.modiapersonoversikt.rest.persondata

import no.nav.modiapersonoversikt.consumer.dkif.Dkif
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PersondataConfig {
    @Bean
    open fun persondataService(
        pdl: PdlOppslagService,
        @Qualifier("DkifSoap") dkif: Dkif.Service,
        norgApi: NorgApi,
        personV3: PersonV3,
        skjermedePersonerApi: SkjermedePersonerApi,
        tilgangskontroll: Tilgangskontroll,
        kodeverk: EnhetligKodeverk.Service
    ): PersondataService {
        return PersondataServiceImpl(
            pdl,
            dkif,
            norgApi,
            personV3,
            skjermedePersonerApi,
            tilgangskontroll,
            kodeverk
        )
    }
}
