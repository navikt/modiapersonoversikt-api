package no.nav.modiapersonoversikt.infrastructure.kabac.impl

import no.nav.modiapersonoversikt.infrastructure.kabac.AttributeValue
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

class PolicyDecisionPointImpl : Kabac.PolicyDecisionPoint {
    private val providerRegister = mutableMapOf<Key<*>, Kabac.PolicyInformationPoint<*>>()

    override fun install(informationPoint: Kabac.PolicyInformationPoint<*>): Kabac.PolicyDecisionPoint {
        providerRegister[informationPoint.key] = informationPoint
        return this
    }

    override fun createEvaluationContext(attributes: List<AttributeValue<*>>): Kabac.EvaluationContext {
        return EvaluationContextImpl(providerRegister.values + attributes)
    }
}
