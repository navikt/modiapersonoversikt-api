package no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll

import no.nav.brukerdialog.security.context.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.GrunninfoService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rsbac.*
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.modiaRolle
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.tilgangTilBruker

class GenerellContext(
        private val ldap : LDAPService,
        private val grunninfo : GrunninfoService
) {
    fun hentSaksbehandlerId(): String = SubjectHandler.getSubjectHandler().uid
    fun hentRoller(saksbehandlerId: String): List<String> = ldap.hentRollerForVeileder(saksbehandlerId)
    fun hentDiskresjonkode(fnr: String): String? = grunninfo.hentBrukerInfo(fnr).diskresjonskode
}

private val modiaRoller = setOf("0000-ga-bd06_modiagenerelltilgang", "0000-ga-modia-oppfolging", "0000-ga-syfo-sensitiv")
internal class Policies {
    companion object {
        val modiaRolle: Policy<GenerellContext> = Policy("Saksbehandler har ikke tilgang til modia") {
            if (it.hentRoller(it.hentSaksbehandlerId()).any(modiaRoller::contains))
                DecisionEnums.PERMIT
            else
                DecisionEnums.DENY
        }

        val kode6: PolicyGenerator<GenerellContext, String> = PolicyGenerator("Saksbehandler har ikke tilgang til kode6 brukere") {
            if (it.context.hentDiskresjonkode(it.data) == "6") {
                if (it.context.hentRoller(it.context.hentSaksbehandlerId()).contains("0000-GA-GOSYS_KODE6"))
                    DecisionEnums.PERMIT
                else
                    DecisionEnums.DENY
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        val kode7: PolicyGenerator<GenerellContext, String> = PolicyGenerator("Saksbehandler har ikke tilgang til kode7 brukere") {
            if (it.context.hentDiskresjonkode(it.data) == "7") {
                if (it.context.hentRoller(it.context.hentSaksbehandlerId()).contains("0000-GA-GOSYS_KODE7"))
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
    }
}

class Tilgangskontroll(context: GenerellContext) {
    private val rsbac: RSBAC<GenerellContext> = RSBACImpl(context)

    fun tilgangTilModia() = rsbac.check(modiaRolle)
    fun tilgangTilBruker(fnr: String) = rsbac.check(tilgangTilBruker.with(fnr))
}