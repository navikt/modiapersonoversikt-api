package no.nav.modiapersonoversikt.config.artifact.kjerneinfo

import no.nav.modiapersonoversikt.config.endpoint.v1.egenansatt.EgenAnsattV1EndpointConfig
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.egenansatt.EgenAnsattService
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.egenansatt.EgenAnsattServiceImpl
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.UnleashProxySwitcher
import no.nav.tjeneste.pip.egen.ansatt.v1.EgenAnsattV1
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(EgenAnsattV1EndpointConfig::class)
open class EgenAnsattWrapper {
    @Autowired
    lateinit var egenAnsattV1: EgenAnsattV1

    @Bean
    open fun egenAnsattService(unleashService: UnleashService, skjermedePersonerApi: SkjermedePersonerApi): EgenAnsattService {
        return UnleashProxySwitcher.createSwitcher(
            featureToggle = Feature.BRUK_SKJERMET_PERSON,
            unleashService = unleashService,
            ifEnabled = skjermedePersonerApi,
            ifDisabled = EgenAnsattServiceImpl(egenAnsattV1)
        )
    }
}
