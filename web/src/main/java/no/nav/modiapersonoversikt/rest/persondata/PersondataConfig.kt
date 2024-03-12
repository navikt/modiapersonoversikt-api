package no.nav.modiapersonoversikt.rest.persondata

import no.nav.modiapersonoversikt.consumer.krr.Krr
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolging
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.kontonummer.KontonummerService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.personoversikt.common.kabac.Kabac
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PersondataConfig {
    @Bean
    open fun persondataService(
        pdl: PdlOppslagService,
        krrService: Krr.Service,
        norgApi: NorgApi,
        skjermedePersonerApi: SkjermedePersonerApi,
        kontonummerService: KontonummerService,
        oppfolgingConfig: ArbeidsrettetOppfolging.Service,
        policyEnforcementPoint: Kabac.PolicyEnforcementPoint,
        kodeverk: EnhetligKodeverk.Service,
    ): PersondataService {
        return PersondataServiceImpl(
            pdl,
            krrService,
            norgApi,
            skjermedePersonerApi,
            kontonummerService,
            oppfolgingConfig,
            policyEnforcementPoint,
            kodeverk,
        )
    }
}
