package no.nav.sbl.dialogarena.modiabrukerdialog.web.config

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSHentNAVAnsattFagomradeListeRequest
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSHentNAVEnhetListeRequest
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.*
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet
import no.nav.common.auth.SubjectHandler
import no.nav.sbl.dialogarena.abac.AbacClient
import no.nav.sbl.dialogarena.abac.AbacRequest
import no.nav.sbl.dialogarena.abac.AbacResponse
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseLesService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollContext
import no.nav.sbl.util.EnvironmentUtils
import org.slf4j.LoggerFactory
import java.util.*

open class TilgangskontrollContextImpl(
        private val abacClient: AbacClient,
        private val ldap: LDAPService,
        private val ansattService: GOSYSNAVansatt,
        private val henvendelseLesService: HenvendelseLesService,
        private val unleashService: UnleashService
) : TilgangskontrollContext {
    private val logger = LoggerFactory.getLogger(TilgangskontrollContext::class.java)

    override fun checkAbac(request: AbacRequest): AbacResponse = abacClient.evaluate(request)
    override fun hentSaksbehandlerId(): Optional<String> = SubjectHandler.getIdent().map(String::toUpperCase)
    override fun harSaksbehandlerRolle(rolle: String) = hentSaksbehandlerRoller().contains(rolle.toLowerCase())
    override fun hentTemagrupperForSaksbehandler(valgtEnhet: String): Set<String> = try {
        val ansattFagomraderRequest = ASBOGOSYSHentNAVAnsattFagomradeListeRequest()
        ansattFagomraderRequest.ansattId = hentSaksbehandlerId().orElseThrow { RuntimeException("Fant ikke saksbehandlerIdent") }
        ansattFagomraderRequest.enhetsId = valgtEnhet

        ansattService
                .hentNAVAnsattFagomradeListe(ansattFagomraderRequest)
                .fagomrader
                .map { it.fagomradeKode }
                .toSet()
    } catch (e: Exception) {
        when (e) {
            is HentNAVAnsattFagomradeListeFaultGOSYSNAVAnsattIkkeFunnetMsg ->
                logger.warn("Fant ikke ansatt med ident {}.", hentSaksbehandlerId(), e)
            is HentNAVAnsattFagomradeListeFaultGOSYSGeneriskMsg ->
                logger.warn("Feil oppsto under henting av ansatt fagområdeliste for enhet med enhetsId {}.", valgtEnhet, e)
            else ->
                logger.warn("Ukjent feil ved henting av fagområdelsite for ident {} enhet {}.", hentSaksbehandlerId(), valgtEnhet, e)
        }

        emptySet()
    }

    override fun hentSaksbehandlereMedTilgangTilHastekassering(): List<String> {
        return EnvironmentUtils.getRequiredProperty("HASTEKASSERING_TILGANG", "")
                .let {
                    it.split(",")
                            .map(String::trim)
                            .map(String::toUpperCase)
                }
    }

    override fun alleBehandlingsIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean {
        return henvendelseLesService.alleBehandlingsIderTilhorerBruker(fnr, behandlingsIder)
    }

    override fun featureToggleEnabled(featureToggle: String): Boolean = unleashService.isEnabled(featureToggle)

    private fun hentSaksbehandlerRoller(): List<String> =
            hentSaksbehandlerId()
                    .map(ldap::hentRollerForVeileder)
                    .orElse(emptyList())
                    .map { it.toLowerCase() }
}
