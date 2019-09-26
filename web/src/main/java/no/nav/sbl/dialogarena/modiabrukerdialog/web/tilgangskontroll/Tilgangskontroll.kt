package no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll

import no.nav.sbl.dialogarena.modiabrukerdialog.web.rsbac.*
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.aaregTilgang
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.behandlingsIderTilhorerFnrPolicy
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.endreAdresse
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.endreKontonummer
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.endreNavn
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.henvendelsesIdTilhorerFnrPolicy
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.kontorSperretMelding
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.modiaRolle
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.oksosSperretMelding
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.plukkOppgave
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.saksbehandlerKanHastekasserePolicy
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.tilgangTilBrukerPolicy
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.tilgangTilOppfoling
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.tilgangTilPesysPolicy
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.Companion.tilgangTilTema
import javax.ws.rs.ForbiddenException

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

class Policies {
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

        @JvmField
        val tilgangTilBrukerPolicy = PolicySetGenerator(
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

        @JvmField
        val behandlingsIderTilhorerFnrPolicy = PolicyGenerator<TilgangskontrollContext, BehandlingsIdTilgangData>({ "Ikke alle behandlingsIder tilhørte medsendt fødselsnummer. Spørring gjort av ${context.hentSaksbehandlerId()}" }) {
            if (context.alleBehandlingsIderTilhorerBruker(data.fnr, data.behandlingsIder))
                DecisionEnums.PERMIT
            else
                DecisionEnums.DENY
        }

        val henvendelsesIdTilhorerFnrPolicy = PolicyGenerator<TilgangskontrollContext, BehandlingsIdTilgangData>({ "Ikke alle behandlingsIder tilhørte medsendt fødselsnummer. Spørring gjort av ${context.hentSaksbehandlerId()}" }) {
            if (context.alleHenvendelseIderTilhorerBruker(data.fnr, data.behandlingsIder))
                DecisionEnums.PERMIT
            else
                DecisionEnums.DENY
        }

        val saksbehandlerKanHastekasserePolicy = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til hastekassering" }) {
            val identer = hentSaksbehandlereMedTilgangTilHastekassering()
            if (identer.contains(hentSaksbehandlerId())) DecisionEnums.PERMIT else DecisionEnums.DENY
        }
    }
}

data class BehandlingsIdTilgangData(val fnr: String, val behandlingsIder: List<String>)
data class TilgangTilTemaData(val valgtEnhet: String, val tema: String)

class Tilgangskontroll(context: TilgangskontrollContext) : RSBACImpl<TilgangskontrollContext>(context, { ForbiddenException(it) })

fun RSBAC<TilgangskontrollContext>.tilgangTilBruker(fnr: String) = this.check(tilgangTilBrukerPolicy.with(fnr))
fun RSBAC<TilgangskontrollContext>.tilgangTilModia() = this.check(modiaRolle)
fun RSBAC<TilgangskontrollContext>.tilgangTilTema(valgtEnhet: String, tema: String) = this.check(tilgangTilTema.with(TilgangTilTemaData(valgtEnhet, tema)))
fun RSBAC<TilgangskontrollContext>.tilgangTilEndreAdresse() = this.check(endreAdresse)
fun RSBAC<TilgangskontrollContext>.tilgangTilEndreNavn() = this.check(endreNavn)
fun RSBAC<TilgangskontrollContext>.tilgangTilEndreKontonummer() = this.check(endreKontonummer)
fun RSBAC<TilgangskontrollContext>.tilgangTilMelding(kontorSperretEnhet: String?) = this.check(kontorSperretMelding.with(kontorSperretEnhet))
fun RSBAC<TilgangskontrollContext>.tilgangTilOksos(fnr: String) = this.check(oksosSperretMelding.with(fnr))
fun RSBAC<TilgangskontrollContext>.tilgangTilAareg() = this.check(aaregTilgang)
fun RSBAC<TilgangskontrollContext>.tilgangTilOppfolging() = this.check(tilgangTilOppfoling)
fun RSBAC<TilgangskontrollContext>.tilgangTilPesys() = this.check(tilgangTilPesysPolicy)
fun RSBAC<TilgangskontrollContext>.tilgangTilPlukkOppgave() = this.check(plukkOppgave)
fun RSBAC<TilgangskontrollContext>.saksbehandlerKanHastekassere() = this.check(saksbehandlerKanHastekasserePolicy)
fun RSBAC<TilgangskontrollContext>.behandlingsIderTilhorerBruker(fnr: String, behandlingsIder: List<String>) =
        this.check(behandlingsIderTilhorerFnrPolicy.with(BehandlingsIdTilgangData(fnr, behandlingsIder)))
fun RSBAC<TilgangskontrollContext>.henvendelsesIdTilhorerBruker(fnr: String, henvendelseIder: List<String>) =
        this.check(henvendelsesIdTilhorerFnrPolicy.with(BehandlingsIdTilgangData(fnr, henvendelseIder)))

