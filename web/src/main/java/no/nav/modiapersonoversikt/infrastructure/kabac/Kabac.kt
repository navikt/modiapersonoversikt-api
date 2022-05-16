package no.nav.modiapersonoversikt.infrastructure.kabac

import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

object Kabac {
    interface AttributeKey<TValue> {
        val key: Key<TValue>
    }
    interface EvaluationContext : EvaluationReporter {
        fun <TValue> getValue(attributeKey: AttributeKey<TValue>): TValue
        fun <TValue> getValue(key: Key<TValue>): TValue
    }

    interface EvaluationReporter {
        fun report(message: String): EvaluationReporter
        fun indent(): EvaluationReporter
        fun unindent(): EvaluationReporter
        fun getReport(): String
    }

    interface Policy {
        val key: Key<Policy>
        fun evaluate(ctx: EvaluationContext): Decision
    }

    interface PolicyInformationPoint<TValue> : AttributeKey<TValue> {
        fun provide(ctx: EvaluationContext): TValue?
    }
    interface PolicyDecisionPoint {
        fun install(informationPoint: PolicyInformationPoint<*>): PolicyDecisionPoint
        fun createEvaluationContext(attributes: List<AttributeValue<*>>): EvaluationContext
    }

    interface PolicyEnforcementPoint {
        val bias: Decision.Type

        fun createEvaluationContext(attributes: List<AttributeValue<*>>): EvaluationContext
        fun evaluatePolicies(
            combiningAlgorithm: CombiningAlgorithm = CombiningAlgorithm.denyOverride,
            bias: Decision.Type = this.bias,
            attributes: List<AttributeValue<*>> = emptyList(),
            policies: List<Policy>
        ): Decision

        fun evaluatePoliciesWithReport(
            combiningAlgorithm: CombiningAlgorithm = CombiningAlgorithm.denyOverride,
            bias: Decision.Type = this.bias,
            attributes: List<AttributeValue<*>> = emptyList(),
            policies: List<Policy>
        ): Pair<Decision, String>

        fun evaluatePolicy(
            bias: Decision.Type = this.bias,
            attributes: List<AttributeValue<*>> = emptyList(),
            policy: Policy
        ): Decision

        fun evaluatePolicyWithReport(
            bias: Decision.Type = this.bias,
            attributes: List<AttributeValue<*>> = emptyList(),
            policy: Policy
        ): Pair<Decision, String>

        fun evaluatePolicyWithContext(
            bias: Decision.Type = this.bias,
            ctx: EvaluationContext,
            policy: Policy
        ): Decision

        fun evaluatePolicyWithContextWithReport(
            bias: Decision.Type = this.bias,
            ctx: EvaluationContext,
            policy: Policy
        ): Pair<Decision, String>
    }
}
