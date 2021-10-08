package no.nav.modiapersonoversikt.rest.persondata

import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.egenansatt.EgenAnsattService
import no.nav.modiapersonoversikt.service.dkif.Dkif
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
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
        organisasjonEnhetV2Service: OrganisasjonEnhetV2Service,
        personV3: PersonV3,
        egenAnsattService: EgenAnsattService,
        tilgangskontroll: Tilgangskontroll,
        kodeverk: EnhetligKodeverk.Service
    ): PersondataService {
        return PersondataServiceImpl(
            pdl,
            dkif,
            organisasjonEnhetV2Service,
            personV3,
            egenAnsattService,
            tilgangskontroll,
            kodeverk
        )
    }
}