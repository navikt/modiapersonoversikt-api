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

    sealed class Decision(internal var type: Type, open val message: String? = null) {
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
            val name = this.type.name.lowercase().replaceFirstChar { it.titlecase() }
            val message = if (message == null) "" else "($message)"
            return "$name$message"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Decision

            if (type != other.type) return false
            if (message != other.message) return false

            return true
        }

        override fun hashCode(): Int {
            var result = type.hashCode()
            result = 31 * result + (message?.hashCode() ?: 0)
            return result
        }

        enum class Type { PERMIT, DENY, NOT_APPLICABLE }
        class Permit : Decision(Type.PERMIT)
        class NotApplicable(message: String? = null) : Decision(Type.NOT_APPLICABLE, message)
        class Deny(message: String) : Decision(Type.DENY, message)
    }

    class MissingAttributeProviderException(message: String) : IllegalStateException(message)
    class MissingAttributeValueException(message: String) : IllegalStateException(message)
    class CycleInPipUsageException(message: String) : IllegalStateException(message)

    fun interface Policy {
        fun evaluate(ctx: EvaluationContext): Decision
    }

    val bias: Decision.Type
    fun <TValue : Any> install(provider: AttributeProvider<TValue>): Kabac
    fun evaluatePolicy(
        bias: Decision.Type = this.bias,
        attributes: List<AttributeProvider<*>> = emptyList(),
        policy: Policy
    ): Decision

    fun evaluatePolicies(
        combiner: CombiningAlgorithm = CombiningAlgorithm.denyOverride,
        bias: Decision.Type = this.bias,
        attributes: List<AttributeProvider<*>> = emptyList(),
        policies: List<Policy>
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
        override fun evaluatePolicy(
            bias: Decision.Type,
            attributes: List<AttributeProvider<*>>,
            policy: Policy
        ): Decision {
            val ctx = EvaluationContext(providerRegister.values + attributes)
            return policy
                .evaluate(ctx)
                .withBias(bias)
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
    }
}
