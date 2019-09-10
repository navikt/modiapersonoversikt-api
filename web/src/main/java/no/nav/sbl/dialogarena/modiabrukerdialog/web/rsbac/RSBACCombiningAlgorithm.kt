package no.nav.sbl.dialogarena.modiabrukerdialog.web.rsbac

abstract class CombiningAlgo {
    abstract fun <CONTEXT> combine(policies: List<Combinable<CONTEXT>>, context: CONTEXT): Decision

    companion object {
        @JvmField
        val denyOverride: CombiningAlgo = DenyOverride()
        @JvmField
        val firstApplicable: CombiningAlgo = FirstApplicable()
    }
}

// Inspirert av https://www.axiomatics.com/blog/understanding-xacml-combining-algorithms/
private class DenyOverride : CombiningAlgo() {
    override fun <CONTEXT> combine(policies: List<Combinable<CONTEXT>>, context: CONTEXT): Decision {
        var combinedDecision = Decision("No matching rule found", DecisionEnums.NOT_APPLICABLE)
        for (policy: Combinable<CONTEXT> in policies) {
            val ruleDecision = policy.invoke(context)

            combinedDecision = when (ruleDecision) {
                DecisionEnums.DENY -> return Decision(policy.getMessage(), ruleDecision)
                DecisionEnums.NOT_APPLICABLE -> combinedDecision
                else -> Decision(policy.getMessage(), ruleDecision)
            }
        }
        return combinedDecision
    }
}

private class FirstApplicable : CombiningAlgo() {
    override fun <CONTEXT> combine(policies: List<Combinable<CONTEXT>>, context: CONTEXT): Decision {
        var combinedDecision = Decision("No matching rule found", DecisionEnums.NOT_APPLICABLE)
        for (policy: Combinable<CONTEXT> in policies) {
            val ruleDecision = policy.invoke(context)

            if (ruleDecision.isApplicable()) {
                return Decision(policy.getMessage(), ruleDecision)
            }

            combinedDecision = when (combinedDecision.decision) {
                DecisionEnums.DENY, DecisionEnums.PERMIT -> combinedDecision
                DecisionEnums.NOT_APPLICABLE -> Decision(policy.getMessage(), ruleDecision)
            }
        }
        return combinedDecision
    }
}