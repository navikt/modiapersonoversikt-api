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
    override fun combine(policies: List<Kabac.Policy>) = object : Kabac.Policy {
        override val key = Key<Kabac.Policy>("Combining policies $overrideValue overrides")

        override fun evaluate(ctx: EvaluationContext): Kabac.Decision {
            var combined: Kabac.Decision = Kabac.Decision.NotApplicable("No applicable policy found")
            for (policy in policies) {
                ctx.addToReport(policy::class.java.simpleName).tab()
                val decision = policy.evaluate(ctx)
                ctx.addToReport("Decision: $decision")

                combined = when (decision.type) {
                    overrideValue -> {
                        ctx.untab()
                            .addToReport("Last decision matched overridevalue: $overrideValue")
                            .addToReport("Further execution not needed")
                        return decision
                    }
                    Kabac.Decision.Type.NOT_APPLICABLE -> combined
                    else -> decision
                }
                ctx.untab()
            }
            ctx.addToReport("Result: $combined")
            return combined
        }
    }
}

private class FirstApplicable : CombiningAlgorithm {
    override fun combine(policies: List<Kabac.Policy>) = object : Kabac.Policy {
        override val key = Key<Kabac.Policy>("Combining policies first applicable")
        override fun evaluate(ctx: EvaluationContext): Kabac.Decision {
            for (policy in policies) {
                ctx.addToReport(policy::class.java.simpleName).tab()
                val decision = policy.evaluate(ctx)
                ctx.addToReport("Decision: $decision")
                if (decision.isApplicable()) {
                    ctx.untab()
                        .addToReport("Last decision was applicable: $decision")
                        .addToReport("Further execution not needed")
                    return decision
                }
                ctx.untab()
            }
            ctx.addToReport("Result: No application policy found")
            return Kabac.Decision.NotApplicable("No applicable policy found")
        }
    }
}
