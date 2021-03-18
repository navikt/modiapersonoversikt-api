package no.nav.sbl.dialogarena.rsbac

abstract class CombiningAlgo {
    abstract fun <CONTEXT> combine(policies: List<Combinable<CONTEXT>>, context: CONTEXT): Decision

    companion object {
        @JvmField
        val denyOverride: CombiningAlgo = DenyOverride2()

        @JvmField
        val permitOverride: CombiningAlgo = PermitOverride()

        @JvmField
        val firstApplicable: CombiningAlgo = FirstApplicable()
    }
}

// Inspirert av https://www.axiomatics.com/blog/understanding-xacml-combining-algorithms/
private open class DecisionOverride(val overrideValue: DecisionEnums) : CombiningAlgo() {
    override fun <CONTEXT> combine(policies: List<Combinable<CONTEXT>>, context: CONTEXT): Decision {
        var combinedDecision = Decision("No matching rule found", DecisionEnums.NOT_APPLICABLE)
        for (policy: Combinable<CONTEXT> in policies) {
            val ruleDecision: Decision = policy.invoke(context)

            combinedDecision = when (ruleDecision.value) {
                overrideValue -> return ruleDecision
                DecisionEnums.NOT_APPLICABLE -> combinedDecision
                else -> ruleDecision
            }
        }
        return combinedDecision
    }
}

private class PermitOverride : DecisionOverride(DecisionEnums.PERMIT)
private class DenyOverride2 : DecisionOverride(DecisionEnums.DENY)

private class FirstApplicable : CombiningAlgo() {
    override fun <CONTEXT> combine(policies: List<Combinable<CONTEXT>>, context: CONTEXT): Decision {
        var combinedDecision = Decision("No matching rule found", DecisionEnums.NOT_APPLICABLE)
        for (policy: Combinable<CONTEXT> in policies) {
            val ruleDecision = policy.invoke(context)

            if (ruleDecision.value.isApplicable()) {
                return ruleDecision
            }

            combinedDecision = when (combinedDecision.value) {
                DecisionEnums.DENY, DecisionEnums.PERMIT -> combinedDecision
                DecisionEnums.NOT_APPLICABLE -> ruleDecision
            }
        }
        return combinedDecision
    }
}
