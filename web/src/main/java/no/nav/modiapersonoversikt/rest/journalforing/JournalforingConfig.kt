package no.nav.modiapersonoversikt.rest.journalforing

import no.nav.modiapersonoversikt.legacy.api.service.saker.SakerService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.UnleashProxySwitcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class JournalforingConfig {
    @Autowired
    private lateinit var unleashService: UnleashService

    @Autowired
    private lateinit var sakerService: SakerService

    @Autowired
    private lateinit var sfHenvendelseService: SfHenvendelseService

    @Bean
    open fun journalforingApi(): JournalforingApi {
        return UnleashProxySwitcher.createSwitcher(
            featureToggle = Feature.USE_SALESFORCE_DIALOG,
            unleashService = unleashService,
            ifEnabled = SFJournalforing(sakerService, sfHenvendelseService),
            ifDisabled = JoarkJournalforing(sakerService)
        )
    }
}
