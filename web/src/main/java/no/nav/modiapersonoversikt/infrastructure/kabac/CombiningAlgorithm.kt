package no.nav.modiapersonoversikt.infrastructure.kabac

interface CombiningAlgorithm {
    context(EvaluationContext) fun process(policies: List<Kabac.Policy>): Kabac.Decision

    companion object {
        val firstApplicable : CombiningAlgorithm = FirstApplicable()
        val denyOverride : CombiningAlgorithm = DecisionOverride(Kabac.Decision.Type.DENY)
        val permitOverride : CombiningAlgorithm = DecisionOverride(Kabac.Decision.Type.PERMIT)
    }
}

private class DecisionOverride(val overrideValue: Kabac.Decision.Type) : CombiningAlgorithm {
    context(EvaluationContext)override fun process(policies: List<Kabac.Policy>): Kabac.Decision {
        var combined: Kabac.Decision = Kabac.Decision.NotApplicable("No applicable policy found")
        for (policy in policies) {
            val decision = policy.evaluate()

            combined = when (decision.type) {
                overrideValue -> return decision
                Kabac.Decision.Type.NOT_APPLICABLE -> combined
                else -> decision
            }
        }
        return combined
    }
}

private class FirstApplicable : CombiningAlgorithm {
    context(EvaluationContext) override fun process(policies: List<Kabac.Policy>): Kabac.Decision {
        for (policy in policies) {
            val decision = policy.evaluate()
            if (decision.isApplicable()) {
                return decision
            }
        }
        return Kabac.Decision.NotApplicable("No applicable policy found")
    }
}