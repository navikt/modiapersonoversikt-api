package no.nav.modiapersonoversikt.infrastructure.kabac.utils

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac

class EvaluationContext(providers: List<Kabac.AttributeProvider<*>>) {
    private val evaluationRegister = providers.associateBy { it.key }

    fun <TValue : Any> getValue(provider: Kabac.AttributeKey<TValue>): TValue? = getValue(provider.key)
    fun <TValue : Any> requireValue(provider: Kabac.AttributeKey<TValue>): TValue = requireValue(provider.key)
    fun <TValue : Any> requireValue(key: Key<TValue>): TValue = requireNotNull(getValue(key)) {
        "Value for $key cannot be null"
    }

    fun <TValue : Any> getValue(key: Key<TValue>): TValue? {
        val provider = requireNotNull(evaluationRegister[key]) {
            "Could not find provider for $key in evaluation"
        }

        return provider.provide(this) as TValue?
    }
}
