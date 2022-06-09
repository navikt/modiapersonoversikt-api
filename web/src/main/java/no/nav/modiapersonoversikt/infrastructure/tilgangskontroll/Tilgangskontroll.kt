package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import no.nav.modiapersonoversikt.infrastructure.TjenestekallLogger
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.infrastructure.kabac.AttributeValue
import no.nav.modiapersonoversikt.infrastructure.kabac.CombiningAlgorithm
import no.nav.modiapersonoversikt.infrastructure.kabac.Decision
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit

interface Tilgangskontroll {
    fun check(policy: PolicyWithAttributes): TilgangskontrollInstance
}
interface TilgangskontrollInstance : Tilgangskontroll {
    fun <S> get(audit: Audit.AuditDescriptor<in S>, block: () -> S): S
    fun getDecision(): Decision
}
private typealias NoAccessHandler = (String) -> java.lang.RuntimeException

data class PolicyWithAttributes(
    val policy: Kabac.Policy,
    val attributes: List<AttributeValue<*>>
)

private class Instance(
    private val enforcementPoint: Kabac.PolicyEnforcementPoint,
    private val noAccessHandler: NoAccessHandler,
) : TilgangskontrollInstance {
    private val policies = mutableListOf<PolicyWithAttributes>()
    private var combiningAlgorithm = CombiningAlgorithm.denyOverride
    private var bias = enforcementPoint.bias

    override fun check(policy: PolicyWithAttributes): TilgangskontrollInstance {
        this.policies.add(policy)
        return this
    }

    override fun <S> get(audit: Audit.AuditDescriptor<in S>, block: () -> S): S {
        return when (val decision = getDecision()) {
            is Decision.Permit -> runCatching(block)
                .onSuccess(audit::log)
                .onFailure(audit::failed)
                .getOrThrow()
            is Decision.Deny -> {
                audit.denied(decision.message)
                throw noAccessHandler(decision.message)
            }
            is Decision.NotApplicable -> {
                throw noAccessHandler(decision.message ?: "No applicable policy found")
            }
        }
    }

    override fun getDecision(): Decision {
        val attributes = policies
            .flatMap { it.attributes }
            .distinctBy { it.key }
        val ctx = enforcementPoint.createEvaluationContext(attributes)
        val policy = combiningAlgorithm.combine(policies.map { it.policy })

        val (decision, report) = enforcementPoint.evaluatePolicyWithContextWithReport(
            bias = bias,
            ctx = ctx,
            policy = policy
        )
        TjenestekallLogger.logger.info(TjenestekallLogger.format("policy-report: ${getCallId()}", report))

        return decision
    }
}

class TilgangskontrollKabac(
    private val enforcementPoint: Kabac.PolicyEnforcementPoint,
    private val noAccessHandler: NoAccessHandler
) : Tilgangskontroll {
    override fun check(policy: PolicyWithAttributes): TilgangskontrollInstance {
        return Instance(enforcementPoint, noAccessHandler).check(policy)
    }
}
