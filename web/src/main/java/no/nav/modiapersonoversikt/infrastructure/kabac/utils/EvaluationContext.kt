package no.nav.modiapersonoversikt.infrastructure.kabac.utils

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac

class EvaluationContext(providers: List<Kabac.AttributeProvider<*>>) : EvaluationReporter by EvaluationReporter.Impl() {
    private val evaluationRegister = providers.associateBy { it.key }
    private val cache = mutableMapOf<Key<*>, Any?>()
    private val keystack = KeyStack()

    fun <TValue : Any> getValue(provider: Kabac.AttributeKey<TValue>): TValue? = getValue(provider.key)
    fun <TValue : Any> requireValue(provider: Kabac.AttributeKey<TValue>): TValue = requireValue(provider.key)
    fun <TValue : Any> requireValue(key: Key<TValue>): TValue {
        return getValue(key) ?: throw Kabac.MissingAttributeValueException("Value for $key cannot be null")
    }
    fun <TValue : Any> getValue(key: Key<TValue>): TValue? {
        return keystack.withCycleDetection(key) {
            if (cache.containsKey(key)) {
                val value = cache[key] as TValue?
                addToReport("Requested $key, cache-hit: $value")
                value
            } else {
                val provider = evaluationRegister[key]
                if (provider == null) {
                    addToReport("Requested $key, no provider found")
                    throw Kabac.MissingAttributeProviderException("Could not find provider for $key")
                }

                (provider.provide(this) as TValue?).also {
                    addToReport("Requested $key, cache-miss: $it")
                    cache[key] = it
                }
            }
        }
    }

    companion object {
        operator fun invoke(vararg providers: Kabac.AttributeProvider<*>) = EvaluationContext(providers.toList())
    }
}
