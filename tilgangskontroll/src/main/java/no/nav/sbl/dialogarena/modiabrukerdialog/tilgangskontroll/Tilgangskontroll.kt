package no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll

import no.nav.sbl.dialogarena.abac.AbacResponse
import no.nav.sbl.dialogarena.abac.Decision as AbacDecision
import no.nav.sbl.dialogarena.rsbac.*
import org.slf4j.LoggerFactory
import javax.ws.rs.ForbiddenException

fun AbacResponse.toDecisionEnum(): DecisionEnums = when(this.getDecision()) {
    AbacDecision.Deny -> DecisionEnums.DENY
    AbacDecision.Permit -> DecisionEnums.PERMIT
    else -> DecisionEnums.NOT_APPLICABLE
}
fun AbacResponse.toDecision(denyReason: AbacResponse.() -> String): Decision = when(this.getDecision()) {
    AbacDecision.Deny -> Decision(denyReason(this), DecisionEnums.DENY)
    AbacDecision.Permit -> Decision("", DecisionEnums.PERMIT)
    else -> Decision("", DecisionEnums.NOT_APPLICABLE)
}

class Policies {
    companion object {
        @JvmField
        val tilgangTilModia = RulePolicy<TilgangskontrollContext>() {
            checkAbac(AbacPolicies.tilgangTilModia())
                    .toDecision {
                        "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til modia. Årsak: ${getCause()}"
                    }
        }

        @JvmField
        val tilgangTilDiskresjonskode = PolicyGenerator<TilgangskontrollContext, String>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til $data" }) {
            val diskresjonskode = data
            if (arrayOf("6", "SPSF").contains(diskresjonskode)) {
                if (context.harSaksbehandlerRolle("0000-GA-GOSYS_KODE6"))
                    DecisionEnums.PERMIT
                else
                    DecisionEnums.DENY
            } else if (arrayOf("7", "SPFO").contains(diskresjonskode)) {
                if (context.harSaksbehandlerRolle("0000-GA-GOSYS_KODE7"))
                    DecisionEnums.PERMIT
                else
                    DecisionEnums.DENY
            } else {
                DecisionEnums.NOT_APPLICABLE
            }
        }

        @JvmField
        val tilgangTilBruker = RulePolicyGenerator<TilgangskontrollContext, String> {
            context.checkAbac(AbacPolicies.tilgangTilBruker(data))
                    .toDecision {
                        "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til ${data}. Årsak: ${getCause()}"
                    }
        }

        @JvmField
        val kanPlukkeOppgave = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til plukk oppgave" }) {
            checkAbac(AbacPolicies.kanPlukkeOppgave())
                    .toDecisionEnum()
        }

        @JvmField
        val tilgangTilTema = PolicyGenerator<TilgangskontrollContext, TilgangTilTemaData>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til tema: ${data.tema} enhet: ${data.valgtEnhet}" }) {
            val temaer = context.hentTemagrupperForSaksbehandler(data.valgtEnhet)
            if (temaer.contains(data.tema)) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.DENY
            }
        }

        @JvmField
        val tilgangTilKontorsperretMelding = PolicyGenerator<TilgangskontrollContext, TilgangTilKontorSperreData>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til kontorsperret melding" }) {
            when {
                data.ansvarligEnhet == null || data.ansvarligEnhet.isEmpty() -> DecisionEnums.PERMIT
                data.valgtEnhet == data.ansvarligEnhet -> DecisionEnums.PERMIT
                else -> DecisionEnums.DENY
            }
        }

        @JvmField
        val tilgangTilOksosMelding = PolicyGenerator<TilgangskontrollContext, TilgangTilOksosSperreData>({ "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til økonomisk sosialhjelp" }) {
            when {
                context.harSaksbehandlerRolle("0000-GA-Okonomisk_Sosialhjelp") -> DecisionEnums.PERMIT
                data.valgtEnhet == data.brukersEnhet -> DecisionEnums.PERMIT
                else -> DecisionEnums.DENY
            }
        }

        @JvmField
        val behandlingsIderTilhorerBruker = PolicyGenerator<TilgangskontrollContext, BehandlingsIdTilgangData>({ "Ikke alle behandlingsIder tilhørte medsendt fødselsnummer. Spørring gjort av ${context.hentSaksbehandlerId()}" }) {
            if (context.alleBehandlingsIderTilhorerBruker(data.fnr, data.behandlingsIder))
                DecisionEnums.PERMIT
            else
                DecisionEnums.DENY
        }

        @JvmField
        val kanHastekassere = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til hastekassering" }) {
            hentSaksbehandlerId()
                    .map { ident ->
                        val identer = hentSaksbehandlereMedTilgangTilHastekassering()
                        if (identer.contains(ident)) DecisionEnums.PERMIT else DecisionEnums.DENY
                    }.orElse(DecisionEnums.DENY)
        }
    }
}

data class TilgangTilKontorSperreData(val valgtEnhet: String, val ansvarligEnhet: String?)
data class TilgangTilOksosSperreData(val valgtEnhet: String, val brukersEnhet: String?)
data class BehandlingsIdTilgangData(val fnr: String, val behandlingsIder: List<String>)
data class TilgangTilTemaData(val valgtEnhet: String, val tema: String?)

val log = LoggerFactory.getLogger(Tilgangskontroll::class.java)

open class Tilgangskontroll(context: TilgangskontrollContext) : RSBACImpl<TilgangskontrollContext>(context, {
    log.error(it)
    ForbiddenException(it)
})
