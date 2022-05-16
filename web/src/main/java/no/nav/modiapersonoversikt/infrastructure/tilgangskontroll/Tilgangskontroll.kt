package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import no.nav.common.types.identer.AktorId
import no.nav.common.types.identer.EksternBrukerId
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.abac.AbacResponse
import no.nav.modiapersonoversikt.infrastructure.kabac.AttributeValue
import no.nav.modiapersonoversikt.infrastructure.kabac.Decision.Type
import no.nav.modiapersonoversikt.infrastructure.rsbac.*
import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.CommonAttributes
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.PublicPolicies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.TilgangTilBrukerPolicy
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.TilgangTilModiaPolicy
import no.nav.modiapersonoversikt.service.sfhenvendelse.fixKjedeId
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
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

object Policies {
    private val abacTilgangTilModiaExperiment = Scientist.createExperiment<Decision>(
        Scientist.Config(
            name = "internal-abac-tilgang-til-modia",
            experimentRate = Scientist.FixedValueRate(0.0) // Erstattes med unleash rate ved kjøring
        )
    )
    private val abacTilgangTilBrukerExperiment = Scientist.createExperiment<Decision>(
        Scientist.Config(
            name = "internal-abac-tilgang-til-bruker",
            experimentRate = Scientist.FixedValueRate(0.0) // Erstattes med unleash rate ved kjøring
        )
    )
    private val abacTilgangTilTemaExperiment = Scientist.createExperiment<DecisionEnums>(
        Scientist.Config(
            name = "internal-abac-tilgang-til-tema",
            experimentRate = Scientist.FixedValueRate(0.0) // Erstattes med unleash rate ved kjøring
        )
    )
    private val abacHenvendelserTilhorerBrukerExperiment = Scientist.createExperiment<DecisionEnums>(
        Scientist.Config(
            name = "internal-abac-henvendelse-tilhorer-bruker",
            experimentRate = Scientist.FixedValueRate(0.0) // Erstattes med unleash rate ved kjøring
        )
    )
    private val abacKanBrukerInternalExperiment = Scientist.createExperiment<DecisionEnums>(
        Scientist.Config(
            name = "internal-abac-kan-bruke-internal",
            experimentRate = Scientist.FixedValueRate(0.0) // Erstattes med unleash rate ved kjøring
        )
    )

    @JvmField
    val tilgangTilModia = RulePolicy<TilgangskontrollContext> {
        abacTilgangTilModiaExperiment.run(
            unleash = unleash(),
            control = {
                checkAbac(AbacPolicies.tilgangTilModia())
                    .toDecision {
                        "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til modia. Årsak: ${getCause()}"
                    }
            },
            experiment = {
                kabac()
                    .evaluatePolicy(policy = TilgangTilModiaPolicy)
                    .toDecisionEnum()
            }
        )
    }

    @JvmField
    val tilgangTilBruker = RulePolicyGenerator<TilgangskontrollContext, String> {
        abacTilgangTilBrukerExperiment.run(
            unleash = context.unleash(),
            control = {
                context.checkAbac(AbacPolicies.tilgangTilBruker(data))
                    .toDecision {
                        "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til $data. Årsak: ${getCause()}"
                    }
            },
            experiment = { internalTilgangTilBruker.with(Fnr(data)).invoke(context) }
        )
    }

    @JvmField
    val tilgangTilBrukerMedAktorId = RulePolicyGenerator<TilgangskontrollContext, String> {
        abacTilgangTilBrukerExperiment.run(
            unleash = context.unleash(),
            control = {
                context.checkAbac(AbacPolicies.tilgangTilBrukerMedAktorId(data))
                    .toDecision {
                        "Saksbehandler (${context.hentSaksbehandlerId()}) har ikke tilgang til $data. Årsak: ${getCause()}"
                    }
            },
            experiment = { internalTilgangTilBruker.with(AktorId(data)).invoke(context) }
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
        abacTilgangTilTemaExperiment.run(
            unleash = context.unleash(),
            control = {
                val temaer = context.hentTemagrupperForSaksbehandler(data.valgtEnhet)
                if (temaer.contains(data.tema)) {
                    DecisionEnums.PERMIT
                } else {
                    DecisionEnums.DENY
                }
            },
            experiment = {
                if (data.tema == null) {
                    DecisionEnums.PERMIT
                } else {
                    val (policy, attributes) = PublicPolicies.tilgangTilTema(
                        enhet = EnhetId(data.valgtEnhet),
                        tema = data.tema
                    )
                    context.kabac()
                        .evaluatePolicy(policy = policy, attributes = attributes)
                        .toDecisionEnum()
                }
            }
        )
    }

    @JvmField
    val behandlingsIderTilhorerBruker = PolicyGenerator<TilgangskontrollContext, BehandlingsIdTilgangData>({ "Ikke alle behandlingsIder tilhørte medsendt fødselsnummer. Spørring gjort av ${context.hentSaksbehandlerId()}" }) {
        abacHenvendelserTilhorerBrukerExperiment.run(
            unleash = context.unleash(),
            control = {
                if (context.alleBehandlingsIderTilhorerBruker(data.fnr, data.behandlingsIder)) {
                    DecisionEnums.PERMIT
                } else {
                    DecisionEnums.DENY
                }
            },
            experiment = {
                val kjedeId = data.behandlingsIder.map { it.fixKjedeId() }.distinct()
                require(kjedeId.size == 1) {
                    "Fant ${kjedeId.size} unike kjedeIder i samme spørring, men kan bare være 1."
                }
                val (policy, attributes) = PublicPolicies.henvendelseTilhorerBruker(
                    eksternBrukerId = Fnr(data.fnr),
                    kjedeId = kjedeId.first()
                )
                context
                    .kabac()
                    .evaluatePolicy(policy = policy, attributes = attributes)
                    .toDecisionEnum()
            }
        )
    }

    @JvmField
    val sfDialogTilhorerBruker = PolicyGenerator<TilgangskontrollContext, KjedeIdTilgangData>({ "KjedeId tilhørte ikke bruker. Spørring gjort av ${context.hentSaksbehandlerId()}" }) {
        DecisionEnums.DENY
    }

    val kanBrukeInternal = Policy<TilgangskontrollContext>({ "Saksbehandler (${hentSaksbehandlerId()}) har ikke tilgang til internal endepunkter" }) {
        abacKanBrukerInternalExperiment.run(
            unleash = unleash(),
            control = {
                hentSaksbehandlerId()
                    .map { ident ->
                        val identer = hentSaksbehandlereMedTilgangTilInternal()
                        if (identer.contains(ident)) DecisionEnums.PERMIT else DecisionEnums.DENY
                    }.orElse(DecisionEnums.DENY)
            },
            experiment = {
                val (policy, attributes) = PublicPolicies.kanBrukerInternal()
                kabac()
                    .evaluatePolicy(policy = policy, attributes = attributes)
                    .toDecisionEnum()
            }
        )
    }

    private fun Scientist.Experiment<Decision>.run(
        unleash: UnleashService,
        control: () -> Decision,
        experiment: () -> Any?,
    ): Decision = run(
        control = control,
        experiment = experiment,
        dataFields = ::overrideAbacResultComparator,
        overrideRate = Scientist.UnleashRate(unleash, Feature.INTERNAL_ABAC_RATE)
    )
    private fun Scientist.Experiment<DecisionEnums>.run(
        unleash: UnleashService,
        control: () -> DecisionEnums,
        experiment: () -> Any?,
    ): DecisionEnums = run(
        control = control,
        experiment = experiment,
        dataFields = ::overrideAbacResultComparator,
        overrideRate = Scientist.UnleashRate(unleash, Feature.INTERNAL_ABAC_RATE)
    )

    private fun overrideAbacResultComparator(
        control: Decision,
        tryExperiment: Scientist.UtilityClasses.Try<Any?>
    ): Map<String, Any?> = overrideAbacResultComparator(control.value, tryExperiment)

    private fun overrideAbacResultComparator(
        control: DecisionEnums,
        tryExperiment: Scientist.UtilityClasses.Try<Any?>
    ): Map<String, Any?> {
        if (tryExperiment.isFailure) {
            return emptyMap()
        }

        val controlDecision: DecisionEnums = control
        val experiment = tryExperiment.getOrNull()
        return when (experiment) {
            is Decision? -> mapOf("ok" to (controlDecision == experiment?.value))
            is DecisionEnums? -> mapOf("ok" to (controlDecision == experiment))
            else -> mapOf(
                "ok" to false,
                "error" to "Experiment Decision was not of the right type: ${experiment?.javaClass?.simpleName}"
            )
        }
    }
}

private fun no.nav.modiapersonoversikt.infrastructure.kabac.Decision.toDecisionEnum(): DecisionEnums = when (this.type) {
    Type.PERMIT -> DecisionEnums.PERMIT
    Type.DENY -> DecisionEnums.DENY
    Type.NOT_APPLICABLE -> DecisionEnums.NOT_APPLICABLE
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
