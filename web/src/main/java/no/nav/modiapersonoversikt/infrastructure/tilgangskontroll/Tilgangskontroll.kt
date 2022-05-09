package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import no.nav.common.types.identer.AktorId
import no.nav.common.types.identer.EksternBrukerId
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.abac.AbacResponse
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.AttributeValue
import no.nav.modiapersonoversikt.infrastructure.rsbac.*
import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.CommonAttributes
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.TilgangTilBrukerPolicy
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.TilgangTilModiaPolicy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import no.nav.modiapersonoversikt.consumer.abac.Decision as AbacDecision

fun AbacResponse.toDecision(denyReason: AbacResponse.() -> String): Decision = when (this.getDecision()) {
    AbacDecision.Deny -> Decision(denyReason(this), DecisionEnums.DENY)
    AbacDecision.Permit -> Decision("", DecisionEnums.PERMIT)
    else -> Decision("", DecisionEnums.NOT_APPLICABLE)
}

class Policies {
    companion object {
        private val abacTilgangTilModiaExperiment = Scientist.createExperiment<Decision>(
            Scientist.Config(
                name = "internal-abac-tilgang-til-modia",
                experimentRate = Scientist.FixedValueRate(0.01)
            )
        )
        private val abacTilgangTilFnrExperiment = Scientist.createExperiment<Decision>(
            Scientist.Config(
                name = "internal-abac-tilgang-til-fnr",
                experimentRate = Scientist.FixedValueRate(0.01)
            )
        )
        private val abacTilgangTilAktoridExperiment = Scientist.createExperiment<Decision>(
            Scientist.Config(
                name = "internal-abac-tilgang-til-aktorid",
                experimentRate = Scientist.FixedValueRate(0.01)
            )
        )
        private fun overrideAbacResultComparator(control: Decision, tryExperiment: Scientist.UtilityClasses.Try<Any?>): Map<String, Any?> {
            if (tryExperiment.isFailure) {
                return emptyMap()
            }

            val controlDecision = control.value
            val experiment = tryExperiment.getOrThrow()
            return when (experiment) {
                is Decision? -> mapOf("ok" to (controlDecision == experiment?.value))
                else -> mapOf("ok" to false, "error" to "Experiment Decision was not of the right type: ${experiment?.javaClass?.simpleName}")
            }
        }

        @JvmField
        val tilgangTilModia = RulePolicy<TilgangskontrollContext> {
            abacTilgangTilModiaExperiment.run(
                control = {
                    checkAbac(AbacPolicies.tilgangTilModia())
                        .toDecision {
                            "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til modia. Årsak: ${getCause()}"
                        }
                },
                experiment = {
                    internalTilgangTilModia(this)
                },
                dataFields = ::overrideAbacResultComparator
            )
        }

        private val internalTilgangTilModia = Policy<TilgangskontrollContext>({ "Saksbehandler (${this.hentSaksbehandlerId()}) har ikke tilgang til modia" }) {
            kabac()
                .evaluatePolicy(policy = TilgangTilModiaPolicy)
                .toDecisionEnum()
        }

        @JvmField
        val tilgangTilBruker = RulePolicyGenerator<TilgangskontrollContext, String> {
            abacTilgangTilFnrExperiment.run(
                control = {
                    context.checkAbac(AbacPolicies.tilgangTilBruker(data))
                        .toDecision {
                            "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til $data. Årsak: ${getCause()}"
                        }
                },
                experiment = { internalTilgangTilBruker.with(Fnr(data)).invoke(context) },
                dataFields = ::overrideAbacResultComparator
            )
        }

        @JvmField
        val tilgangTilBrukerMedAktorId = RulePolicyGenerator<TilgangskontrollContext, String> {
            abacTilgangTilAktoridExperiment.run(
                control = {
                    context.checkAbac(AbacPolicies.tilgangTilBrukerMedAktorId(data))
                        .toDecision {
                            "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til $data. Årsak: ${getCause()}"
                        }
                },
                experiment = { internalTilgangTilBruker.with(AktorId(data)).invoke(context) },
                dataFields = ::overrideAbacResultComparator
            )
        }

        private val internalTilgangTilBruker = PolicyGenerator<TilgangskontrollContext, EksternBrukerId>({ "" }) {
            val personAttribute = when (data) {
                is Fnr -> AttributeValue(CommonAttributes.FNR, data)
                is AktorId -> AttributeValue(CommonAttributes.AKTOR_ID, data)
                else -> throw UnsupportedOperationException("Støtter ikke andre typer eksternID")
            }
            context.kabac()
                .evaluatePolicy(
                    attributes = listOf(personAttribute),
                    policy = TilgangTilBrukerPolicy
                )
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
        val behandlingsIderTilhorerBruker = PolicyGenerator<TilgangskontrollContext, BehandlingsIdTilgangData>({ "Ikke alle behandlingsIder tilhørte medsendt fødselsnummer. Spørring gjort av ${context.hentSaksbehandlerId()}" }) {
            if (context.alleBehandlingsIderTilhorerBruker(data.fnr, data.behandlingsIder)) {
                DecisionEnums.PERMIT
            } else {
                DecisionEnums.DENY
            }
        }

        @JvmField
        val sfDialogTilhorerBruker = PolicyGenerator<TilgangskontrollContext, KjedeIdTilgangData>({ "KjedeId tilhørte ikke bruker. Spørring gjort av ${context.hentSaksbehandlerId()}" }) {
            DecisionEnums.DENY
        }

        val kanBrukeInternal = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til internal endepunkter" }) {
            hentSaksbehandlerId()
                .map { ident ->
                    val identer = hentSaksbehandlereMedTilgangTilInternal()
                    if (identer.contains(ident)) DecisionEnums.PERMIT else DecisionEnums.DENY
                }.orElse(DecisionEnums.DENY)
        }
    }
}

private fun Kabac.Decision.toDecisionEnum(): DecisionEnums = when (this.type) {
    Kabac.Decision.Type.PERMIT -> DecisionEnums.PERMIT
    Kabac.Decision.Type.DENY -> DecisionEnums.DENY
    Kabac.Decision.Type.NOT_APPLICABLE -> DecisionEnums.NOT_APPLICABLE
}

data class BehandlingsIdTilgangData(val fnr: String, val behandlingsIder: List<String>)
data class KjedeIdTilgangData(val fnr: String, val kjedeId: String)
data class TilgangTilTemaData(val valgtEnhet: String, val tema: String?)

val log: Logger = LoggerFactory.getLogger(Tilgangskontroll::class.java)

open class Tilgangskontroll(context: TilgangskontrollContext) : RSBACImpl<TilgangskontrollContext>(
    context,
    {
        log.error(it)
        ResponseStatusException(HttpStatus.FORBIDDEN, it)
    }
)
