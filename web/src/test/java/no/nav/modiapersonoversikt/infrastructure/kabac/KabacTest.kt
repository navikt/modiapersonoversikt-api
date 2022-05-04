package no.nav.modiapersonoversikt.infrastructure.kabac

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac.Decision
import no.nav.modiapersonoversikt.infrastructure.kabac.providers.AttributeProvider
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.ThrowableAssert.ThrowingCallable
import org.junit.jupiter.api.Test

internal class KabacTest {
    object DummyProvider : Kabac.AttributeProvider<String> {
        override val key: Key<String> = Key("dummy-provider")
        override fun provide(ctx: EvaluationContext): String {
            return "dummy value"
        }
    }

    object DummyDependentProvider : Kabac.AttributeProvider<Int> {
        override val key: Key<Int> = Key("dummy-dependent-provider")
        override fun provide(ctx: EvaluationContext): Int {
            return ctx.requireValue(DummyProvider).length
        }
    }

    object ErrorThrowingProvider : Kabac.AttributeProvider<String> {
        override val key: Key<String> = Key("error-throwing-provider")
        override fun provide(ctx: EvaluationContext): String {
            throw IllegalArgumentException("Something went wrong")
        }
    }

    @Test
    internal fun `installing provider`() {
        val kabac = Kabac()
            .install(DummyProvider)

        val decision: Decision = kabac.evaluatePolicy { ctx ->
            Decision.Deny(ctx.requireValue(DummyProvider))
        }

        assertThat(decision).isEqualTo(Decision.Deny("dummy value"))
    }

    @Test
    internal fun `missing attribute should cause error`() {
        val kabac = Kabac()
            .install(DummyDependentProvider)

        val evaluation = ThrowingCallable {
            kabac.evaluatePolicy { ctx ->
                Decision.Deny(ctx.requireValue(DummyProvider))
            }
        }

        assertThatThrownBy(evaluation)
            .isInstanceOf(Kabac.MissingAttributeException::class.java)
            .hasMessage("Could not find provider for Key(dummy-provider)")
    }

    @Test
    internal fun `dependent provider should get its value from another provider`() {
        val kabac = Kabac()
            .install(DummyProvider)
            .install(DummyDependentProvider)

        val decision: Decision = kabac.evaluatePolicy { ctx ->
            val value: Int = ctx.requireValue(DummyDependentProvider)
            Decision.Deny("Length of string was: $value")
        }

        assertThat(decision).isEqualTo(Decision.Deny("Length of string was: 11"))
    }

    @Test
    internal fun `providing attribute value directly should short-circuit provider chain even if it exists`() {
        val kabac = Kabac()
            .install(DummyProvider)
            .install(DummyDependentProvider)

        val decision: Decision = kabac.evaluatePolicy(
            attributes = listOf(
                AttributeProvider(DummyProvider, "this is a longer value")
            ),
            policy = { ctx ->
                val value: Int = ctx.requireValue(DummyDependentProvider)
                Decision.Deny("Length of string was: $value")
            }
        )

        assertThat(decision).isEqualTo(Decision.Deny("Length of string was: 22"))
    }

    @Test
    internal fun `providing attribute value directly should short-circuit provider chain`() {
        val kabac = Kabac()
            .install(DummyDependentProvider)

        val decision: Decision = kabac.evaluatePolicy(
            attributes = listOf(
                AttributeProvider(DummyProvider, "this is a longer value")
            ),
            policy = { ctx ->
                val value: Int = ctx.requireValue(DummyDependentProvider)
                Decision.Deny("Length of string was: $value")
            }
        )

        assertThat(decision).isEqualTo(Decision.Deny("Length of string was: 22"))
    }

    @Test
    internal fun `provider throwing error should bubble up`() {
        val kabac = Kabac()
            .install(ErrorThrowingProvider)

        val evaluation = ThrowingCallable {
            kabac.evaluatePolicy { ctx ->
                Decision.Deny(ctx.requireValue(ErrorThrowingProvider))
            }
        }

        assertThatThrownBy(evaluation)
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Something went wrong")
    }

    @Test
    internal fun `kabac bias should be applied to decision`() {
        val kabac = Kabac(bias = Decision.Type.PERMIT)

        val decision = kabac.evaluatePolicy {
            Decision.NotApplicable("Doesn't matter")
        }

        assertThat(decision).isEqualTo(Decision.Permit())
    }

    @Test
    internal fun `policyevaluation bias should override kabac bias`() {
        val kabac = Kabac(bias = Decision.Type.PERMIT)

        val decision = kabac.evaluatePolicy(
            bias = Decision.Type.DENY,
            policy = {
                Decision.NotApplicable("Doesn't matter")
            }
        )

        assertThat(decision).isEqualTo(Decision.Deny("No applicable policy found"))
    }

    @Test
    internal fun `not_applicable cannot be set as bias`() {
        assertThatThrownBy { Kabac(bias = Decision.Type.NOT_APPLICABLE) }
            .isInstanceOf(UnsupportedOperationException::class.java)
            .hasMessage("Bias cannot be NOT_APPLICABLE")
    }

    @Test
    internal fun `supplied attributes override registered providers`() {
        val kabac = Kabac()
            .install(DummyProvider)

        val decision = kabac.evaluatePolicy(
            attributes = listOf(AttributeProvider(DummyProvider, "overridden")),
            policy = { ctx ->
                Decision.Deny(ctx.requireValue(DummyProvider))
            }
        )

        assertThat(decision).isEqualTo(Decision.Deny("overridden"))
    }
}
