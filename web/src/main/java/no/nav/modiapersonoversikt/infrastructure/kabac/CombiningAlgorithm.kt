package no.nav.modiapersonoversikt.infrastructure.kabac

import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

interface CombiningAlgorithm {
    fun combine(policies: List<Kabac.Policy>): Kabac.Policy

    companion object {
        val firstApplicable: CombiningAlgorithm = FirstApplicable()
        val denyOverride: CombiningAlgorithm = DecisionOverride(Decision.Type.DENY)
        val permitOverride: CombiningAlgorithm = DecisionOverride(Decision.Type.PERMIT)
    }
}

private class DecisionOverride(val overrideValue: Decision.Type) : CombiningAlgorithm {
    override fun combine(policies: List<Kabac.Policy>) = object : Kabac.Policy {
        override val key = Key<Kabac.Policy>("Combinging policies by ${overrideValue.name.lowercase()}-override rule")

        override fun evaluate(ctx: Kabac.EvaluationContext): Decision {
            var decision: Decision = Decision.NotApplicable("No applicable policy found")
            for (policy in policies) {
                ctx.report(policy.key.name).indent()
                val policyDecision = policy
                    .evaluate(ctx)
                    .also { ctx.report("Decision: $it") }

                decision = when (policyDecision.type) {
                    overrideValue -> {
                        ctx
                            .unindent()
                            .report("Last decision matches override value. Stopping policy evaluation.")
                        return policyDecision
                    }
                    Decision.Type.NOT_APPLICABLE -> decision
                    else -> policyDecision
                }
                ctx.unindent()
            }
            return decision
        }
    }
}

private class FirstApplicable : CombiningAlgorithm {
    override fun combine(policies: List<Kabac.Policy>) = object : Kabac.Policy {
        override val key = Key<Kabac.Policy>("Combinging policies by first-applicable rule")

        override fun evaluate(ctx: Kabac.EvaluationContext): Decision {
            for (policy in policies) {
                ctx.report(policy.key.name).indent()
                val decision = policy
                    .evaluate(ctx)
                    .also { ctx.report("Decision: $it") }

                if (decision.isApplicable()) {
                    ctx
                        .unindent()
                        .report("Last decision was applicable. Stopping policy evaluation.")
                    return decision
                }
                ctx.unindent()
            }
            return Decision.NotApplicable("No applicable policy found")
        }
    }
}
