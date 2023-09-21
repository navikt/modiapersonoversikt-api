package no.nav.modiapersonoversikt.service.kontonummer

import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class KontonummerServiceConfig {
    @Autowired
    private lateinit var tps: PersonV3

    @Bean
    open fun kontonummerService() = TpsKontonummerService(tps)
}
