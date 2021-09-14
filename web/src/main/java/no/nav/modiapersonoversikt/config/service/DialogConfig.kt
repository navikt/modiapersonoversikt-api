package no.nav.modiapersonoversikt.config.service

import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.api.service.HenvendelseUtsendingService
import no.nav.modiapersonoversikt.legacy.api.service.OppgaveBehandlingService
import no.nav.modiapersonoversikt.legacy.api.service.kodeverk.StandardKodeverk
import no.nav.modiapersonoversikt.legacy.api.service.ldap.LDAPService
import no.nav.modiapersonoversikt.legacy.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService
import no.nav.modiapersonoversikt.rest.dialog.apis.DialogApi
import no.nav.modiapersonoversikt.rest.dialog.apis.DialogDelsvarApi
import no.nav.modiapersonoversikt.rest.dialog.apis.DialogMerkApi
import no.nav.modiapersonoversikt.rest.dialog.henvendelse.HenvendelseDelsvar
import no.nav.modiapersonoversikt.rest.dialog.henvendelse.HenvendelseDialog
import no.nav.modiapersonoversikt.rest.dialog.henvendelse.HenvendelseDialogMerk
import no.nav.modiapersonoversikt.rest.dialog.salesforce.SfLegacyDelsvarController
import no.nav.modiapersonoversikt.rest.dialog.salesforce.SfLegacyDialogController
import no.nav.modiapersonoversikt.rest.dialog.salesforce.SfLegacyDialogMerkController
import no.nav.modiapersonoversikt.service.henvendelse.DelsvarService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.UnleashProxySwitcher
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType
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
    private lateinit var kodeverk: StandardKodeverk

    @Autowired
    private lateinit var henvendelseService: HenvendelseBehandlingService

    @Autowired
    private lateinit var henvendelseUtsendingService: HenvendelseUtsendingService

    @Autowired
    private lateinit var behandleHenvendelsePortType: BehandleHenvendelsePortType

    @Autowired
    private lateinit var delsvarService: DelsvarService

    @Bean
    open fun dialogApi(): DialogApi {
        return UnleashProxySwitcher.createSwitcher(
            featureToggle = Feature.USE_SALESFORCE_DIALOG,
            unleashService = unleashService,
            ifEnabled = SfLegacyDialogController(
                tilgangskontroll,
                sfHenvendelseService,
                oppgaveBehandlingService,
                ldapService,
                kodeverk
            ),
            ifDisabled = HenvendelseDialog(
                henvendelseService,
                henvendelseUtsendingService,
                oppgaveBehandlingService
            )
        )
    }

    @Bean
    open fun dialogMerkApi(): DialogMerkApi {
        return UnleashProxySwitcher.createSwitcher(
            featureToggle = Feature.USE_SALESFORCE_DIALOG,
            unleashService = unleashService,
            ifEnabled = SfLegacyDialogMerkController(
                tilgangskontroll,
                sfHenvendelseService,
                oppgaveBehandlingService
            ),
            ifDisabled = HenvendelseDialogMerk(
                behandleHenvendelsePortType,
                oppgaveBehandlingService,
                tilgangskontroll
            )
        )
    }

    @Bean
    open fun dialogDelsvarApi(): DialogDelsvarApi {
        return UnleashProxySwitcher.createSwitcher(
            featureToggle = Feature.USE_SALESFORCE_DIALOG,
            unleashService = unleashService,
            ifEnabled = SfLegacyDelsvarController(),
            ifDisabled = HenvendelseDelsvar(
                delsvarService
            )
        )
    }
}
