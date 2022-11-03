package no.nav.modiapersonoversikt.consumer.dkif

import DigDirServiceImpl
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.UnleashProxySwitcher
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
open class DkifConfig {
    @Bean(name = ["DkifSoap"])
    open fun defaultDkifService(dkifV1: DigitalKontaktinformasjonV1): Dkif.Service {
        return DkifServiceImpl(dkifV1)
    }

    @Bean(name = ["DigDirRest"])
    open fun restDigDirService(machineToMachineTokenClient: MachineToMachineTokenClient): Dkif.Service {
        return DigDirServiceImpl(
            EnvironmentUtils.getRequiredProperty("DIG_DIR_REST_URL"),
            machineToMachineTokenClient
        )
    }

    @Bean
    @Primary
    open fun dkifService(
        @Qualifier("DkifSoap") dkifSoapService: Dkif.Service,
        @Qualifier("DigDirRest") digDirRestService: Dkif.Service,
        unleash: UnleashService
    ): Dkif.Service {
        return UnleashProxySwitcher.createSwitcher(
            featureToggle = Feature.USE_REST_DIG_DIR_PROXY,
            unleashService = unleash,
            ifEnabled = digDirRestService,
            ifDisabled = dkifSoapService
        )
    }
}
