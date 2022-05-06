package no.nav.modiapersonoversikt.infrastructure.kabac.utils

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac

data class AttributeValue<T : Any>(override val key: Key<T>, private val value: T?) : Kabac.AttributeProvider<T> {
    override fun provide(ctx: EvaluationContext): T? = value

    companion object {
        operator fun <TValue : Any> invoke(provider: Kabac.AttributeProvider<TValue>, value: TValue?) = AttributeValue(provider.key, value)
    }
}
