package no.nav.modiapersonoversikt.infrastructure.kabac

import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

data class AttributeValue<TValue>(
    override val key: Key<TValue>,
    private val value: TValue
) : Kabac.PolicyInformationPoint<TValue> {
    override fun provide(ctx: Kabac.EvaluationContext): TValue = value

    companion object {
        operator fun <TValue : Any> invoke(
            provider: Kabac.PolicyInformationPoint<TValue>,
            value: TValue
        ) = AttributeValue(provider.key, value)
    }
}
