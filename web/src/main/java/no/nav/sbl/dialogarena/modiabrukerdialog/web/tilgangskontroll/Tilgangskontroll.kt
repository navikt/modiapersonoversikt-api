package no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSHentNAVAnsattFagomradeListeRequest
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFagomradeListeFaultGOSYSGeneriskMsg
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFagomradeListeFaultGOSYSNAVAnsattIkkeFunnetMsg
import no.nav.brukerdialog.security.context.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.GrunninfoService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rsbac.*
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.modiaRolle
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.tilgangTilBruker
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.tilgangTilTema
import org.slf4j.LoggerFactory

class GenerellContext(
        private val ldap : LDAPService,
        private val grunninfo : GrunninfoService,
        private val ansattService: GOSYSNAVansatt
) {
    private val logger = LoggerFactory.getLogger(GenerellContext::class.java)

    fun hentSaksbehandlerId(): String = SubjectHandler.getSubjectHandler().uid
    fun hentSaksbehandlerRoller(): List<String> = ldap.hentRollerForVeileder(hentSaksbehandlerId()).map { it.toLowerCase() }
    fun harSaksbehandlerRolle(rolle: String) = hentSaksbehandlerRoller().contains(rolle.toLowerCase())
    fun hentDiskresjonkode(fnr: String): String? = grunninfo.hentBrukerInfo(fnr).diskresjonskode
    fun hentTemagrupperForSaksbehandler(valgtEnhet: String): Set<String> = try {
        val ansattFagomraderRequest = ASBOGOSYSHentNAVAnsattFagomradeListeRequest()
        ansattFagomraderRequest.ansattId = hentSaksbehandlerId()
        ansattFagomraderRequest.enhetsId = valgtEnhet

        ansattService
                .hentNAVAnsattFagomradeListe(ansattFagomraderRequest)
                .fagomrader
                .map { it.fagomradeKode }
                .toSet()
    } catch (e: Exception) {
        when (e) {
            is HentNAVAnsattFagomradeListeFaultGOSYSGeneriskMsg ->
                logger.warn("Feil oppsto under henting av ansatt fagområdeliste for enhet med enhetsId {}.", valgtEnhet, e)
            is HentNAVAnsattFagomradeListeFaultGOSYSNAVAnsattIkkeFunnetMsg ->
                logger.warn("Fant ikke ansatt med ident {}.", hentSaksbehandlerId(), e)
            else ->
                logger.warn("Ukjent feil ved henting av fagområdelsite for ident {} enhet {}.", hentSaksbehandlerId(), valgtEnhet, e)
        }

        emptySet()
    }
}

private val modiaRoller = setOf("0000-ga-bd06_modiagenerelltilgang", "0000-ga-modia-oppfolging", "0000-ga-syfo-sensitiv")
internal class Policies {
    companion object {
        val modiaRolle: Policy<GenerellContext> = Policy({ "Saksbehandler (${it.hentSaksbehandlerId()}) har ikke tilgang til modia" }) {
            if (modiaRoller.any { rolle -> it.harSaksbehandlerRolle(rolle) })
                DecisionEnums.PERMIT
            else
                DecisionEnums.DENY
        }

        val kode6: PolicyGenerator<GenerellContext, String> = PolicyGenerator({ "Saksbehandler (${it.context.hentSaksbehandlerId()}) har ikke tilgang til kode6 brukere" }) {
            if (it.context.hentDiskresjonkode(it.data) == "6") {
                if (it.context.harSaksbehandlerRolle("0000-GA-GOSYS_KODE6"))
                    DecisionEnums.PERMIT
                else
                    DecisionEnums.DENY
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        val kode7: PolicyGenerator<GenerellContext, String> = PolicyGenerator({ "Saksbehandler (${it.context.hentSaksbehandlerId()}) har ikke tilgang til kode7 brukere" }) {
            if (it.context.hentDiskresjonkode(it.data) == "7") {
                if (it.context.harSaksbehandlerRolle("0000-GA-GOSYS_KODE7"))
                    DecisionEnums.PERMIT
                else
                    DecisionEnums.DENY
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        val tilgangTilBruker = PolicySetGenerator(
                policies = listOf(modiaRolle.asGenerator(), kode6, kode7)
        )

        val tilgangTilTema = PolicyGenerator<GenerellContext, TilgangTilTemaData>({ "Saksbehandler (${it.context.hentSaksbehandlerId()}) har ikke tilgang til tema: ${it.data.tema} enhet: ${it.data.valgtEnhet}" }) {
            val temaer = it.context.hentTemagrupperForSaksbehandler(it.data.valgtEnhet)
            if (temaer.contains(it.data.tema)) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.DENY
            }
        }
    }
}

data class TilgangTilTemaData(val valgtEnhet: String, val tema: String)

class Tilgangskontroll(context: GenerellContext) {
    private val rsbac: RSBAC<GenerellContext> = RSBACImpl(context)

    fun tilgangTilModia() = rsbac.check(modiaRolle)
    fun tilgangTilBruker(fnr: String) = rsbac.check(tilgangTilBruker.with(fnr))
    fun tilgangTilTema(valgtEnhet: String, tema: String) = rsbac.check(tilgangTilTema.with(TilgangTilTemaData(valgtEnhet, tema)))
}