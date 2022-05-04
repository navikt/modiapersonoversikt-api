package no.nav.modiapersonoversikt.infrastructure.kabac.providers

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

class AttributeProvider<T : Any>(override val key: Key<T>, private val value: T?) : Kabac.AttributeProvider<T> {
    override fun provide(ctx: EvaluationContext): T? = value

    companion object {
        operator fun <TValue : Any> invoke(provider: Kabac.AttributeProvider<TValue>, value: TValue?) = AttributeProvider(provider.key, value)
    }
}
