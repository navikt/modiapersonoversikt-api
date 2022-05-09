package no.nav.modiapersonoversikt.infrastructure.kabac

import no.nav.modiapersonoversikt.infrastructure.kabac.utils.CombiningAlgorithm
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

interface Kabac {
    interface AttributeKey<TValue : Any> {
        val key: Key<TValue>
    }
    interface AttributeProvider<TValue : Any> : AttributeKey<TValue> {
        fun provide(ctx: EvaluationContext): TValue?
    }

    interface Policy {
        val key: Key<Policy>
        fun evaluate(ctx: EvaluationContext): Decision
    }

    sealed class Decision(var type: Type) {
        fun isApplicable(): Boolean = when (this) {
            is Permit, is Deny -> true
            is NotApplicable -> false
        }

        fun withBias(bias: Type): Decision {
            if (!isApplicable()) {
                return when (bias) {
                    Type.PERMIT -> Permit()
                    Type.DENY -> Deny("No applicable policy found")
                    Type.NOT_APPLICABLE -> throw UnsupportedOperationException("Bias cannot be NOT_APPLICABLE")
                }
            }
            return this
        }
        override fun toString(): String {
            return this.type.name.lowercase().replaceFirstChar { it.titlecase() }
        }

        override fun equals(other: Any?): Boolean {
            if (other is Decision) {
                return type == other.type
            }
            return false
        }

        override fun hashCode(): Int {
            return type.hashCode()
        }

        enum class Type { PERMIT, DENY, NOT_APPLICABLE }
        class Permit : Decision(Type.PERMIT)
        class NotApplicable(val message: String? = null) : Decision(Type.NOT_APPLICABLE) {
            override fun toString(): String {
                return "NotApplicable($message)"
            }
        }
        class Deny(val message: String) : Decision(Type.DENY) {
            override fun toString(): String {
                return "Deny($message)"
            }
        }
    }

    class MissingAttributeProviderException(message: String) : IllegalStateException(message)
    class MissingAttributeValueException(message: String) : IllegalStateException(message)
    class CycleInPipUsageException(message: String) : IllegalStateException(message)

    val bias: Decision.Type
    fun <TValue : Any> install(provider: AttributeProvider<TValue>): Kabac

    fun createEvaluationContext(attributes: List<AttributeProvider<*>> = emptyList()): EvaluationContext
    fun evaluatePolicies(
        combiner: CombiningAlgorithm = CombiningAlgorithm.denyOverride,
        bias: Decision.Type = this.bias,
        attributes: List<AttributeProvider<*>> = emptyList(),
        policies: List<Policy>
    ): Decision

    fun evaluatePolicy(
        bias: Decision.Type = this.bias,
        attributes: List<AttributeProvider<*>> = emptyList(),
        policy: Policy
    ): Decision

    fun evaluatePolicyWithContext(
        bias: Decision.Type = this.bias,
        ctx: EvaluationContext,
        policy: Policy
    ): Decision

    class Impl(override val bias: Decision.Type = Decision.Type.DENY) : Kabac {
        init {
            if (bias == Decision.Type.NOT_APPLICABLE) {
                throw UnsupportedOperationException("Bias cannot be NOT_APPLICABLE")
            }
        }

        /**
         * Kabac root provider register
         */
        private val providerRegister = mutableMapOf<Key<*>, AttributeProvider<*>>()
        override fun <TValue : Any> install(provider: AttributeProvider<TValue>): Kabac {
            providerRegister[provider.key] = provider
            return this
        }

        override fun createEvaluationContext(attributes: List<AttributeProvider<*>>): EvaluationContext {
            return EvaluationContext(providerRegister.values + attributes)
        }

        override fun evaluatePolicies(
            combiner: CombiningAlgorithm,
            bias: Decision.Type,
            attributes: List<AttributeProvider<*>>,
            policies: List<Policy>
        ): Decision {
            return evaluatePolicy(
                bias = bias,
                attributes = attributes,
                policy = combiner.combine(policies)
            )
        }
        override fun evaluatePolicy(
            bias: Decision.Type,
            attributes: List<AttributeProvider<*>>,
            policy: Policy
        ): Decision {
            return evaluatePolicyWithContext(
                bias = bias,
                ctx = createEvaluationContext(attributes),
                policy = policy
            )
        }

        override fun evaluatePolicyWithContext(
            bias: Decision.Type,
            ctx: EvaluationContext,
            policy: Policy
        ): Decision {
            ctx.addToReport(policy.key.name).tab()
            return policy
                .evaluate(ctx)
                .withBias(bias)
                .also { ctx.untab() }
        }
    }
}
