package no.nav.modiapersonoversikt.infrastructure.kabac.utils

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac

interface CombiningAlgorithm {
    fun combine(policies: List<Kabac.Policy>): Kabac.Policy

    companion object {
        val firstApplicable: CombiningAlgorithm = FirstApplicable()
        val denyOverride: CombiningAlgorithm = DecisionOverride(Kabac.Decision.Type.DENY)
        val permitOverride: CombiningAlgorithm = DecisionOverride(Kabac.Decision.Type.PERMIT)
    }
}

private class DecisionOverride(val overrideValue: Kabac.Decision.Type) : CombiningAlgorithm {
    override fun combine(policies: List<Kabac.Policy>) = Kabac.Policy { ctx ->
        var combined: Kabac.Decision = Kabac.Decision.NotApplicable("No applicable policy found")
        for (policy in policies) {
            val decision = policy.evaluate(ctx)

            combined = when (decision.type) {
                overrideValue -> return@Policy decision
                Kabac.Decision.Type.NOT_APPLICABLE -> combined
                else -> decision
            }
        }
        combined
    }
}

private class FirstApplicable : CombiningAlgorithm {
    override fun combine(policies: List<Kabac.Policy>) = Kabac.Policy { ctx ->
        for (policy in policies) {
            val decision = policy.evaluate(ctx)
            if (decision.isApplicable()) {
                return@Policy decision
            }
        }
        Kabac.Decision.NotApplicable("No applicable policy found")
    }
}
