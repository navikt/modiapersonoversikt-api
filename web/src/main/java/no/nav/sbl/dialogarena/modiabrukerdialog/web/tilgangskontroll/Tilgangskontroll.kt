package no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll

import no.nav.sbl.dialogarena.modiabrukerdialog.web.rsbac.*
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.modiaRolle
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.tilgangTilBruker
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.tilgangTilTema

private val modiaRoller = setOf("0000-ga-bd06_modiagenerelltilgang", "0000-ga-modia-oppfolging", "0000-ga-syfo-sensitiv")

internal class Policies {
    companion object {
        val modiaRolle = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til modia" }) {
            if (modiaRoller.any { rolle -> harSaksbehandlerRolle(rolle) })
                DecisionEnums.PERMIT
            else
                DecisionEnums.DENY
        }

        val kode6 = PolicyGenerator<TilgangskontrollContext, String>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til kode6 brukere" }) {
            if (context.hentDiskresjonkode(data) == "6") {
                if (context.harSaksbehandlerRolle("0000-GA-GOSYS_KODE6"))
                    DecisionEnums.PERMIT
                else
                    DecisionEnums.DENY
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        val kode7 = PolicyGenerator<TilgangskontrollContext, String>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til kode7 brukere" }) {
            if (context.hentDiskresjonkode(data) == "7") {
                if (context.harSaksbehandlerRolle("0000-GA-GOSYS_KODE7"))
                    DecisionEnums.PERMIT
                else
                    DecisionEnums.DENY
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        val brukerUtenEnhet = PolicyGenerator<TilgangskontrollContext, String>({ "Bruker har ingen enhet" }) {
            if (context.hentBrukersEnhet(data).isNullOrBlank()) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        val tilgangTilLokalKontor = PolicyGenerator<TilgangskontrollContext, String>({ "" }) {
            val brukersEnhet = context.hentBrukersEnhet(data)
            if (context.hentSaksbehandlerLokalEnheter().contains(brukersEnhet)) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        val nasjonalTilgang = PolicyGenerator<TilgangskontrollContext, String>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har nasjonal tilgang" }) {
            if (context.harSaksbehandlerRolle("0000-GA-GOSYS_NASJONAL") || context.harSaksbehandlerRolle("0000-GA-GOSYS_UTVIDBAR_TIL_NASJONAL")) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        val regionalTilgang = PolicyGenerator<TilgangskontrollContext, String>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har regional tilgang" }) {
            val brukersEnhet = context.hentBrukersEnhet(data)
            val harRegionalRolle = context.harSaksbehandlerRolle("0000-GA-GOSYS_REGIONAL") || context.harSaksbehandlerRolle("0000-GA-GOSYS_UTVIDBAR_TIL_REGIONAL")

            if (harRegionalRolle && context.hentSaksbehandlersFylkesEnheter().contains(brukersEnhet)) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        val denyPolicy = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til bruker basert på geografisk tilgang" }) { DecisionEnums.DENY }
        val geografiskTilgang = PolicySetGenerator(
                combining = CombiningAlgo.firstApplicable,
                policies = listOf(brukerUtenEnhet, nasjonalTilgang, tilgangTilLokalKontor, regionalTilgang, denyPolicy.asGenerator())
        )

        val tilgangTilBruker = PolicySetGenerator(
                policies = listOf(modiaRolle.asGenerator(), geografiskTilgang, kode6, kode7)
        )

        val tilgangTilTema = PolicyGenerator<TilgangskontrollContext, TilgangTilTemaData>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til tema: ${data.tema} enhet: ${data.valgtEnhet}" }) {
            val temaer = context.hentTemagrupperForSaksbehandler(data.valgtEnhet)
            if (temaer.contains(data.tema)) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.DENY
            }
        }
    }
}

data class TilgangTilTemaData(val valgtEnhet: String, val tema: String)

class Tilgangskontroll(context: TilgangskontrollContext) {
    private val rsbac: RSBAC<TilgangskontrollContext> = RSBACImpl(context)

    fun tilgangTilModia() = rsbac.check(modiaRolle)
    fun tilgangTilBruker(fnr: String) = rsbac.check(tilgangTilBruker.with(fnr))
    fun tilgangTilTema(valgtEnhet: String, tema: String) = rsbac.check(tilgangTilTema.with(TilgangTilTemaData(valgtEnhet, tema)))
}