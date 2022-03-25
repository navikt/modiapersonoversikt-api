package no.nav.modiapersonoversikt.consumer.dkif

import no.nav.common.utils.EnvironmentUtils
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class DkifConfig {
    @Bean(name = ["DkifSoap"])
    open fun defaultDkifService(dkifV1: DigitalKontaktinformasjonV1): Dkif.Service {
        return DkifServiceImpl(dkifV1)
    }

    @Bean(name = ["DkifRest"])
    open fun restDkifService(): Dkif.Service {
        return DkifServiceRestImpl(
            EnvironmentUtils.getRequiredProperty("DKIF_REST_URL")
        )
    }
}
