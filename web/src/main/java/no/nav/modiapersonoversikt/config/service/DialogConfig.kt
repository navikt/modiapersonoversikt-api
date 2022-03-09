package no.nav.modiapersonoversikt.config.service

import no.nav.modiapersonoversikt.consumer.ldap.LDAPService
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.api.service.OppgaveBehandlingService
import no.nav.modiapersonoversikt.rest.dialog.apis.DialogApi
import no.nav.modiapersonoversikt.rest.dialog.apis.DialogDelsvarApi
import no.nav.modiapersonoversikt.rest.dialog.apis.DialogMerkApi
import no.nav.modiapersonoversikt.rest.dialog.salesforce.SfLegacyDelsvarController
import no.nav.modiapersonoversikt.rest.dialog.salesforce.SfLegacyDialogController
import no.nav.modiapersonoversikt.rest.dialog.salesforce.SfLegacyDialogMerkController
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class DialogConfig {
    @Autowired
    private lateinit var unleashService: UnleashService

    @Autowired
    private lateinit var tilgangskontroll: Tilgangskontroll

    @Autowired
    private lateinit var sfHenvendelseService: SfHenvendelseService

    @Autowired
    private lateinit var oppgaveBehandlingService: OppgaveBehandlingService

    @Autowired
    private lateinit var ldapService: LDAPService

    @Autowired
    private lateinit var kodeverk: EnhetligKodeverk.Service

    @Bean
    open fun dialogApi(): DialogApi {
        return SfLegacyDialogController(
            sfHenvendelseService,
            oppgaveBehandlingService,
            ldapService,
            kodeverk
        )
    }

    @Bean
    open fun dialogMerkApi(): DialogMerkApi {
        return SfLegacyDialogMerkController(
            sfHenvendelseService,
            oppgaveBehandlingService
        )
    }

    @Bean
    open fun dialogDelsvarApi(): DialogDelsvarApi {
        return SfLegacyDelsvarController()
    }
}
