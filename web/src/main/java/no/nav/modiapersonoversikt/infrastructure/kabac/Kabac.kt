package no.nav.modiapersonoversikt.infrastructure.kabac

import no.nav.modiapersonoversikt.infrastructure.kabac.utils.CombiningAlgorithm
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

class Kabac(private val bias: Decision.Type = Decision.Type.DENY) {
    /**
     * Kabac domain
     */
    interface AttributeKey<TValue : Any> {
        val key: Key<TValue>
    }
    interface AttributeProvider<TValue: Any> : AttributeKey<TValue> {
        fun provide(ctx: EvaluationContext): TValue?
    }

    sealed class Decision(internal var type: Type, open val message: String? = null) {
        fun isApplicable(): Boolean = when (this) {
            is Permit, is Deny -> true
            is NotApplicable -> false
        }

        fun withBias(bias: Type): Decision {
            if (!isApplicable()) {
                this.type = bias
            }
            return this
        }

        override fun toString(): String {
            val name = this.type.name.lowercase().replaceFirstChar { it.titlecase() }
            val message = if (message == null) "" else "($message)"
            return "$name$message"
        }

        enum class Type { PERMIT, DENY, NOT_APPLICABLE }
        class Permit : Decision(Type.PERMIT)
        class NotApplicable(message: String?) : Decision(Type.NOT_APPLICABLE, message)
        class Deny(message: String) : Decision(Type.DENY, message)
    }

    fun interface Policy {
        fun evaluate(ctx: EvaluationContext): Decision
    }

    /**
     * Kabac root provider register
     */
    val providerRegister = mutableMapOf<Key<*>, AttributeProvider<*>>()
    fun <TValue: Any> install(provider: AttributeProvider<TValue>) {
        providerRegister[provider.key] = provider
    }

    /**
     * Evaluation functions
     */
    fun evaluate(
        bias: Decision.Type = this.bias,
        attributes: List<AttributeProvider<*>> = emptyList(),
        policy: Policy
    ): Decision {
        val ctx = EvaluationContext(providerRegister.values + attributes)
        return policy
            .evaluate(ctx)
            .withBias(bias)
    }

    fun evaluate(
        combining: CombiningAlgorithm = CombiningAlgorithm.denyOverride,
        bias: Decision.Type = this.bias,
        attributes: List<AttributeProvider<*>> = emptyList(),
        policies: List<Policy>
    ): Decision {
        val ctx = EvaluationContext(providerRegister.values + attributes)
        return combining
            .process(ctx, policies.toList())
            .withBias(bias)
    }
}