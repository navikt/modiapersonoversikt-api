package no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll

import no.nav.sbl.dialogarena.modiabrukerdialog.web.rsbac.*
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.aaregTilgang
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.endreAdresse
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.endreKontonummer
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.endreNavn
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.kontorSperretMelding
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.modiaRolle
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.oksosSperretMelding
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.plukkOppgave
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.tilgangTilBruker
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.tilgangTilOppfoling
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.tilgangTilPesysPolicy
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.tilgangTilTema

private val modiaRoller = setOf("0000-ga-bd06_modiagenerelltilgang", "0000-ga-modia-oppfolging", "0000-ga-syfo-sensitiv")
private val pesysRoller = setOf(
        "0000-GA-PENSJON_SAKSBEHANDLER",
        "0000-GA-Pensjon_VEILEDER",
        "0000-GA-Pensjon_BEGRENSET_VEILEDER",
        "0000-GA-PENSJON_BRUKERHJELPA",
        "0000-GA-PENSJON_SAKSBEHANDLER",
        "0000-GA-PENSJON_KLAGEBEH",
        "0000-GA-Pensjon_Okonomi"
)

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

        val aaregTilgang = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til aareg" }) {
            if (harSaksbehandlerRolle("0000-GA-Aa-register-Lese")) DecisionEnums.PERMIT else DecisionEnums.DENY
        }

        val endreAdresse = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til å endre adresse" }) {
            if (harSaksbehandlerRolle("0000-GA-BD06_EndreKontaktAdresse")) DecisionEnums.PERMIT else DecisionEnums.DENY
        }

        val endreKontonummer = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til å endre bankkontonummer" }) {
            if (harSaksbehandlerRolle("0000-GA-BD06_EndreKontonummer")) DecisionEnums.PERMIT else DecisionEnums.DENY
        }

        val endreNavn = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til å endre navn" }) {
            if (harSaksbehandlerRolle("0000-GA-BD06_EndreNavn")) DecisionEnums.PERMIT else DecisionEnums.DENY
        }

        val kontorSperretMelding = PolicyGenerator<TilgangskontrollContext, String?>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til kontorsperret melding" }) {
            when {
                data == null -> DecisionEnums.PERMIT
                context.hentSaksbehandlerLokalEnheter().contains(data) -> DecisionEnums.PERMIT
                else -> DecisionEnums.DENY
            }
        }

        val oksosSperretMelding = PolicyGenerator<TilgangskontrollContext, String>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til økonomisk sosialhjelp" }) {
            when {
                context.harSaksbehandlerRolle("0000-GA-Okonomisk_Sosialhjelp") -> DecisionEnums.PERMIT
                context.hentSaksbehandlerLokalEnheter().contains(context.hentBrukersEnhet(data)) -> DecisionEnums.PERMIT
                else -> DecisionEnums.DENY
            }
        }

        val tilgangTilOppfoling = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til oppfølging" }) {
            if (harSaksbehandlerRolle("0000-GA-Modia-Oppfolging")) DecisionEnums.PERMIT else DecisionEnums.DENY
        }

        val tilgangTilPesysPolicy = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til pesys" }) {
            if (pesysRoller.any { rolle -> harSaksbehandlerRolle(rolle) })
                DecisionEnums.PERMIT
            else
                DecisionEnums.DENY
        }

        val plukkOppgave = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til plukk oppgave" }) {
            if (harSaksbehandlerRolle("0000-GA-BD06_HentOppgave")) DecisionEnums.PERMIT else DecisionEnums.DENY
        }
    }
}

data class TilgangTilTemaData(val valgtEnhet: String, val tema: String)

class Tilgangskontroll(context: TilgangskontrollContext) {
    private val rsbac: RSBAC<TilgangskontrollContext> = RSBACImpl(context)

    fun tilgangTilModia() = rsbac.check(modiaRolle)
    fun tilgangTilBruker(fnr: String) = rsbac.check(tilgangTilBruker.with(fnr))
    fun tilgangTilTema(valgtEnhet: String, tema: String) = rsbac.check(tilgangTilTema.with(TilgangTilTemaData(valgtEnhet, tema)))
    fun tilgangTilEndreAdresse() = rsbac.check(endreAdresse)
    fun tilgangTilEndreNavn() = rsbac.check(endreNavn)
    fun tilgangTilEndreKontonummer() = rsbac.check(endreKontonummer)
    fun tilgangTilMelding(kontorSperretEnhet: String?) = rsbac.check(kontorSperretMelding.with(kontorSperretEnhet))
    fun tilgangTilOksos(fnr: String) = rsbac.check(oksosSperretMelding.with(fnr))
    fun tilgangTilAareg() = rsbac.check(aaregTilgang)
    fun tilgangTilOppfolging() = rsbac.check(tilgangTilOppfoling)
    fun tilgangTilPesys() = rsbac.check(tilgangTilPesysPolicy)
    fun tilgangTilPlukkOppgave() = rsbac.check(plukkOppgave)
}
