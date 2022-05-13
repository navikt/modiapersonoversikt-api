package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.AttributeValue
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.CombiningAlgorithm
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit

interface Tilgangskontroll {
    fun check(policy: PolicyWithAttributes): TilgangskontrollInstance
}
interface TilgangskontrollInstance : Tilgangskontroll {
    fun <S> get(audit: Audit.AuditDescriptor<in S>, block: () -> S): S
    fun getDecision(): Kabac.Decision
}
private typealias NoAccessHandler = (String) -> java.lang.RuntimeException

data class PolicyWithAttributes(
    val policy: Kabac.Policy,
    val attributes: List<AttributeValue<*>>
)

class TilgangskontrollKabac private constructor(
    private val kabac: Kabac,
    private val noAccessHandler: NoAccessHandler
) : TilgangskontrollInstance {
    private val policies = mutableListOf<PolicyWithAttributes>()
    private var combiningAlgorithm = CombiningAlgorithm.denyOverride
    private var bias = kabac.bias

    companion object {
        operator fun invoke(kabac: Kabac, noAccessHandler: NoAccessHandler): Tilgangskontroll {
            return TilgangskontrollKabac(kabac, noAccessHandler)
        }
    }

    override fun check(policy: PolicyWithAttributes): TilgangskontrollInstance {
        this.policies.add(policy)
        return this
    }

    override fun <S> get(audit: Audit.AuditDescriptor<in S>, block: () -> S): S {
        return when (val decision = getDecision()) {
            is Kabac.Decision.Permit -> runCatching(block)
                .onSuccess(audit::log)
                .onFailure(audit::failed)
                .getOrThrow()
            is Kabac.Decision.Deny -> {
                audit.denied(decision.message)
                throw noAccessHandler(decision.message)
            }
            is Kabac.Decision.NotApplicable -> {
                throw noAccessHandler(decision.message ?: "No applicable policy found")
            }
        }
    }

    override fun getDecision(): Kabac.Decision {
        val attributes = policies
            .flatMap { it.attributes }
            .distinctBy { it.key }
        val ctx = kabac.createEvaluationContext(attributes)
        val policy = combiningAlgorithm.combine(policies.map { it.policy })

        return kabac.evaluatePolicyWithContext(
            bias = bias,
            ctx = ctx,
            policy = policy
        )
    }
}
