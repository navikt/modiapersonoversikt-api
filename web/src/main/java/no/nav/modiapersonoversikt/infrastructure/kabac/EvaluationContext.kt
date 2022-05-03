package no.nav.modiapersonoversikt.infrastructure.kabac

interface EvaluationContext {
    fun <TValue : Any> getValue(provider: Kabac.ProviderKey<TValue>): TValue? = getValue(provider.key)
    fun <TValue : Any> requireValue(provider: Kabac.ProviderKey<TValue>): TValue = requireValue(provider.key)
    fun <TValue : Any> requireValue(key: Key<TValue>): TValue = requireNotNull(getValue(key)) {
        "Value for $key cannot be null"
    }

    fun <TValue : Any> getValue(key: Key<TValue>): TValue?
}

class EvaluationContextImpl(providers: List<Kabac.Provider<*>>) : EvaluationContext {
    private val evaluationRegister = providers.associateBy { it.key }
    override fun <TValue : Any> getValue(key: Key<TValue>): TValue? {
        val provider = requireNotNull(evaluationRegister[key]) {
            "Could not find provider for $key in evaluation"
        }

        return provider.provide() as TValue?
    }
}
