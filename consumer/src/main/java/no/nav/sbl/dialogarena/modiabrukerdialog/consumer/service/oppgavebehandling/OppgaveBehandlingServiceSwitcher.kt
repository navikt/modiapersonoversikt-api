package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling

import no.nav.common.sts.SystemUserTokenProvider
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.rest.RestOppgaveBehandlingServiceImpl
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.UnleashProxySwitcher
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.TildelOppgaveV1

fun createOppgaveBehandlingSwitcher(
    oppgavebehandlingV3: OppgavebehandlingV3,
    tildelOppgaveV1: TildelOppgaveV1,
    oppgaveV3: OppgaveV3,
    kodeverksmapperService: KodeverksmapperService,
    pdlOppslagService: PdlOppslagService,
    ansattService: AnsattService,
    arbeidsfordelingService: ArbeidsfordelingV1Service,
    tilgangskontroll: Tilgangskontroll,
    stsService: SystemUserTokenProvider,
    unleashService: UnleashService
): OppgaveBehandlingService {
    val restClient: OppgaveBehandlingService = RestOppgaveBehandlingServiceImpl(
        kodeverksmapperService,
        pdlOppslagService,
        ansattService,
        arbeidsfordelingService,
        tilgangskontroll,
        stsService
    )
    val soapClient: OppgaveBehandlingService = OppgaveBehandlingServiceImpl(
        oppgavebehandlingV3,
        tildelOppgaveV1,
        oppgaveV3,
        ansattService,
        arbeidsfordelingService,
        tilgangskontroll,
        restClient
    )

    return UnleashProxySwitcher.createSwitcher(
        featureToggle = Feature.USE_REST_OPPGAVE_IMPL,
        unleashService = unleashService,
        ftEnabledImpl = restClient,
        ftDisabledImpl = soapClient
    )
}
