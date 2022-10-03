package no.nav.modiapersonoversikt.rest.journalforing

import no.nav.modiapersonoversikt.service.journalforingsaker.SakerService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class JournalforingConfig {
    @Autowired
    private lateinit var sakerService: SakerService

    @Autowired
    private lateinit var sfHenvendelseService: SfHenvendelseService

    @Bean
    open fun journalforingApi(): JournalforingApi {
        return SFJournalforing(sakerService, sfHenvendelseService)
    }
}
