package no.nav.modiapersonoversikt.service.kontonummer

import no.nav.modiapersonoversikt.consumer.kontoregister.generated.apis.KontoregisterV1Api
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.UnleashProxySwitcher
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class KontonummerServiceConfig {
    @Autowired
    private lateinit var tps: PersonV3

    @Autowired
    private lateinit var kontoregister: KontoregisterV1Api

    @Bean
    open fun kontonummerService(unleash: UnleashService): KontonummerService {
        return UnleashProxySwitcher.createSwitcher(
            featureToggle = Feature.USE_REST_KONTOREGISTER,
            unleashService = unleash,
            ifEnabled = KontonummerRegisterService(kontoregister),
            ifDisabled = TpsKontonummerService(tps)
        )
    }
}
