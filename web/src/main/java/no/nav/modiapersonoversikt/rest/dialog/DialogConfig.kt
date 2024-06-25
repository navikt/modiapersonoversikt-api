package no.nav.modiapersonoversikt.rest.dialog

import no.nav.modiapersonoversikt.kafka.HenvendelseProducer
import no.nav.modiapersonoversikt.rest.dialog.apis.DialogApi
import no.nav.modiapersonoversikt.rest.dialog.apis.DialogDelsvarApi
import no.nav.modiapersonoversikt.rest.dialog.apis.DialogMerkApi
import no.nav.modiapersonoversikt.rest.dialog.salesforce.SfLegacyDelsvarController
import no.nav.modiapersonoversikt.rest.dialog.salesforce.SfLegacyDialogController
import no.nav.modiapersonoversikt.rest.dialog.salesforce.SfLegacyDialogMerkController
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

    @Autowired
    private lateinit var henvendelseProducer: HenvendelseProducer

    @Bean
    open fun dialogApi(): DialogApi {
        return SfLegacyDialogController(
            sfHenvendelseService,
            oppgaveBehandlingService,
            ansattService,
            kodeverk,
            henvendelseProducer,
        )
    }

    @Bean
    open fun dialogMerkApi(): DialogMerkApi {
        return SfLegacyDialogMerkController(
            sfHenvendelseService,
            oppgaveBehandlingService,
        )
    }

    @Bean
    open fun dialogDelsvarApi(): DialogDelsvarApi {
        return SfLegacyDelsvarController()
    }
}
