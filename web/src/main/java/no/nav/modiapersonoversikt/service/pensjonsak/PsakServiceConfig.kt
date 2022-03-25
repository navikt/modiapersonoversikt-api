package no.nav.modiapersonoversikt.service.pensjonsak

import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PsakServiceConfig {
    @Bean
    open fun psakService(pensjonSakV1: PensjonSakV1): PsakService {
        return PsakServiceImpl(pensjonSakV1)
    }
}
