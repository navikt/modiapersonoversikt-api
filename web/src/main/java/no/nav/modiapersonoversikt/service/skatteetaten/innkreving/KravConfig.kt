package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import no.nav.modiapersonoversikt.service.unleash.UnleashService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class KravConfig {
    @Bean
    open fun kravService(
        innkrevingskravClient: InnkrevingskravClient,
        unleash: UnleashService,
    ): InnkrevingskravService = InnkrevingskravService(innkrevingskravClient, unleash)
}
