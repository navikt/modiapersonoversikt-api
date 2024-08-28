package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class KravConfig {
    @Bean
    open fun kravService(skatteetatenInnkrevingClient: SkatteetatenInnkrevingClient): KravService =
        KravService(skatteetatenInnkrevingClient)
}
