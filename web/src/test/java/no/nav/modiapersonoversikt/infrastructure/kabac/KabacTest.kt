package no.nav.modiapersonoversikt.infrastructure.kabac

import no.nav.modiapersonoversikt.infrastructure.kabac.KabacTestUtils.createTestPolicy
import no.nav.modiapersonoversikt.infrastructure.kabac.impl.PolicyDecisionPointImpl
import no.nav.modiapersonoversikt.infrastructure.kabac.impl.PolicyEnforcementPointImpl
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.ThrowableAssert.ThrowingCallable
import org.junit.jupiter.api.Test

internal class KabacTest {
    object DummyProvider : Kabac.PolicyInformationPoint<String> {
        override val key: Key<String> = Key("dummy-provider")
        override fun provide(ctx: Kabac.EvaluationContext): String {
            return "dummy value"
        }
    }

    object DummyDependentProvider : Kabac.PolicyInformationPoint<Int> {
        override val key: Key<Int> = Key("dummy-dependent-provider")
        override fun provide(ctx: Kabac.EvaluationContext): Int {
            return ctx.getValue(DummyProvider).length
        }
    }

    object ErrorThrowingProvider : Kabac.PolicyInformationPoint<String> {
        override val key: Key<String> = Key("error-throwing-provider")
        override fun provide(ctx: Kabac.EvaluationContext): String {
            throw IllegalArgumentException("Something went wrong")
        }
    }

    @Test
    internal fun `installing provider`() {
        val kabac = createPEP(DummyProvider)

        val decision: Decision = kabac.evaluatePolicy(
            policy = createTestPolicy { ctx ->
                Decision.Deny(ctx.getValue(DummyProvider))
            }
        )

        assertThat(decision).isEqualTo(Decision.Deny("dummy value"))
    }

    @Test
    internal fun `missing attribute should cause error`() {
        val kabac = createPEP(DummyDependentProvider)

        val evaluation = ThrowingCallable {
            kabac.evaluatePolicy(
                policy = createTestPolicy { ctx ->
                    Decision.Deny(ctx.getValue(DummyProvider))
                }
            )
        }

        assertThatThrownBy(evaluation)
            .isInstanceOf(KabacException.MissingPolicyInformationPointException::class.java)
            .hasMessage("Could not find provider for Key(dummy-provider)")
    }

    @Test
    internal fun `dependent provider should get its value from another provider`() {
        val kabac = createPEP(DummyProvider, DummyDependentProvider)

        val decision: Decision = kabac.evaluatePolicy(
            policy = createTestPolicy { ctx ->
                val value: Int = ctx.getValue(DummyDependentProvider)
                Decision.Deny("Length of string was: $value")
            }
        )

        assertThat(decision).isEqualTo(Decision.Deny("Length of string was: 11"))
    }

    @Test
    internal fun `providing attribute value directly should short-circuit provider chain even if it exists`() {
        val kabac = createPEP(DummyProvider, DummyDependentProvider)

        val decision: Decision = kabac.evaluatePolicy(
            attributes = listOf(
                AttributeValue(DummyProvider, "this is a longer value")
            ),
            policy = createTestPolicy { ctx ->
                val value: Int = ctx.getValue(DummyDependentProvider)
                Decision.Deny("Length of string was: $value")
            }
        )

        assertThat(decision).isEqualTo(Decision.Deny("Length of string was: 22"))
    }

    @Test
    internal fun `providing attribute value directly should short-circuit provider chain`() {
        val kabac = createPEP(DummyDependentProvider)

        val decision: Decision = kabac.evaluatePolicy(
            attributes = listOf(
                AttributeValue(DummyProvider, "this is a longer value")
            ),
            policy = createTestPolicy { ctx ->
                val value: Int = ctx.getValue(DummyDependentProvider)
                Decision.Deny("Length of string was: $value")
            }
        )

        assertThat(decision).isEqualTo(Decision.Deny("Length of string was: 22"))
    }

    @Test
    internal fun `provider throwing error should bubble up`() {
        val kabac = createPEP(ErrorThrowingProvider)

        val evaluation = ThrowingCallable {
            kabac.evaluatePolicy(
                policy = createTestPolicy { ctx ->
                    Decision.Deny(ctx.getValue(ErrorThrowingProvider))
                }
            )
        }

        assertThatThrownBy(evaluation)
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Something went wrong")
    }

    @Test
    internal fun `kabac bias should be applied to decision`() {
        val kabac = PolicyEnforcementPointImpl(bias = Decision.Type.PERMIT, PolicyDecisionPointImpl())

        val decision = kabac.evaluatePolicy(
            policy = createTestPolicy {
                Decision.NotApplicable("Doesn't matter")
            }
        )

        assertThat(decision).isEqualTo(Decision.Permit())
    }

    @Test
    internal fun `policyevaluation bias should override kabac bias`() {
        val kabac = PolicyEnforcementPointImpl(bias = Decision.Type.PERMIT, PolicyDecisionPointImpl())

        val decision = kabac.evaluatePolicy(
            bias = Decision.Type.DENY,
            policy = createTestPolicy {
                Decision.NotApplicable("Doesn't matter")
            }
        )

        assertThat(decision).isEqualTo(Decision.Deny("No applicable policy found"))
    }

    @Test
    internal fun `not_applicable cannot be set as bias`() {
        assertThatThrownBy { PolicyEnforcementPointImpl(bias = Decision.Type.NOT_APPLICABLE, PolicyDecisionPointImpl()) }
            .isInstanceOf(UnsupportedOperationException::class.java)
            .hasMessage("Bias cannot be 'NOT_APPLICABLE'")
    }

    @Test
    internal fun `supplied attributes override registered providers`() {
        val kabac = createPEP(DummyProvider)

        val decision = kabac.evaluatePolicy(
            attributes = listOf(AttributeValue(DummyProvider, "overridden")),
            policy = createTestPolicy { ctx ->
                Decision.Deny(ctx.getValue(DummyProvider))
            }
        )

        assertThat(decision).isEqualTo(Decision.Deny("overridden"))
    }

    private fun createPEP(vararg pips: Kabac.PolicyInformationPoint<*>): Kabac.PolicyEnforcementPoint {
        return PolicyEnforcementPointImpl(
            policyDecisionPoint = PolicyDecisionPointImpl().apply {
                pips.forEach(::install)
            }
        )
    }
}
