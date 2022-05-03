package no.nav.modiapersonoversikt.infrastructure.kabac

class Kabac : EvaluationContext {
    interface ProviderKey<TValue : Any> {
        val key: Key<TValue>
    }
    interface Provider<TValue: Any> : ProviderKey<TValue> {
        context(EvaluationContext) fun provide(): TValue?
    }

    sealed class Decision(internal val type: Type, open val message: String? = null) {
        fun isApplicable(): Boolean = when (this) {
            is Permit, is Deny -> true
            is NotApplicable -> false
        }

        override fun toString(): String {
            val name = this.type.name.lowercase().replaceFirstChar { it.titlecase() }
            val message = if (message == null) "" else "($message)"
            return "$name$message"
        }

        internal enum class Type { PERMIT, DENY, NOT_APPLICABLE }
        class Permit : Decision(Type.PERMIT)
        class NotApplicable(message: String?) : Decision(Type.NOT_APPLICABLE, message)
        class Deny(message: String) : Decision(Type.DENY, message)
    }

    fun interface Policy {
        context(EvaluationContext) fun evaluate(): Decision
    }

    fun evaluate(
        evaluationProviders: List<Provider<*>> = emptyList(),
        policy: Policy
    ): Decision {
        return with(EvaluationContextImpl(providerRegister.values + evaluationProviders)) {
            policy.evaluate()
        }
    }

    fun evaluate(
        combining: CombiningAlgorithm = CombiningAlgorithm.denyOverride,
        evaluationProviders: List<Provider<*>> = emptyList(),
        policies: List<Policy>
    ): Decision {
        return with(EvaluationContextImpl(providerRegister.values + evaluationProviders)) {
            combining.process(policies.toList())
        }
    }

    /**
     * Kabac root EvaluationContext implementation
     */
    val providerRegister = mutableMapOf<Key<*>, Provider<*>>()
    fun <TValue: Any> install(provider: Provider<TValue>) {
        providerRegister[provider.key] = provider
    }

    override fun <TValue : Any> getValue(key: Key<TValue>): TValue? {
        val provider = requireNotNull(providerRegister[key]) {
            "Could not find provider for $key"
        }
        return provider.provide() as TValue?
    }
}