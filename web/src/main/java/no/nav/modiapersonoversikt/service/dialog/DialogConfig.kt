package no.nav.modiapersonoversikt.service.dialog

import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.oppgavebehandling.OppgaveBehandlingService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class DialogConfig {
    @Autowired
    private lateinit var sfHenvendelseService: SfHenvendelseService

    @Autowired
    private lateinit var oppgaveBehandlingService: OppgaveBehandlingService

    @Autowired
    private lateinit var ansattService: AnsattService

    @Autowired
    private lateinit var kodeverk: EnhetligKodeverk.Service

    @Bean
    open fun dialogService(): DialogService =
        DialogServiceImpl(
            sfHenvendelseService,
            oppgaveBehandlingService,
            ansattService,
            kodeverk,
        )

    @Bean
    open fun dialogMerkService(): DialogMerkService =
        DialogMerkServiceImpl(
            sfHenvendelseService,
            oppgaveBehandlingService,
        )
}
