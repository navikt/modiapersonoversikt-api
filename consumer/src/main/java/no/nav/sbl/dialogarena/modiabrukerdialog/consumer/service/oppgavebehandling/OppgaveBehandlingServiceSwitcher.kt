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
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.TildelOppgaveV1
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class UnleashHandler<T : Any>(
    val feature: Feature,
    val unleashService: UnleashService,
    val ifEnabled: T,
    val ifDisabled: T
) : InvocationHandler {
    val log: Logger = LoggerFactory.getLogger(UnleashHandler::class.java)

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        val nullsafeArgs = args ?: arrayOfNulls<Any>(0)
        return try {
            if (unleashService.isEnabled(feature)) {
                log.warn("[OppgaveBehandlingService] bruker rest-implementasjonen av OppgaveBehandlingService")
                method.invoke(ifEnabled, *nullsafeArgs)
            } else {
                method.invoke(ifDisabled, *nullsafeArgs)
            }
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }
    }
}
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

    val invocationHandler = UnleashHandler(
        unleashService = unleashService,
        feature = Feature.USE_REST_OPPGAVE_IMPL,
        ifDisabled = soapClient,
        ifEnabled = restClient
    )
    val proxy = Proxy.newProxyInstance(
        OppgaveBehandlingService::class.java.classLoader,
        arrayOf(OppgaveBehandlingService::class.java),
        invocationHandler
    )

    return proxy as OppgaveBehandlingService
}
