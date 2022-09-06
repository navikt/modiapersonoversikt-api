package no.nav.modiapersonoversikt.rest.persondata

import no.nav.modiapersonoversikt.consumer.dkif.Dkif
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.kontonummer.KontonummerService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
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
        skjermedePersonerApi: SkjermedePersonerApi,
        kontonummerService: KontonummerService,
        policyEnforcementPoint: Kabac.PolicyEnforcementPoint,
        kodeverk: EnhetligKodeverk.Service
    ): PersondataService {
        return PersondataServiceImpl(
            pdl,
            dkif,
            norgApi,
            skjermedePersonerApi,
            kontonummerService,
            policyEnforcementPoint,
            kodeverk
        )
    }
}
