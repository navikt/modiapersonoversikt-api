package no.nav.modiapersonoversikt.infrastructure.kabac

import no.nav.modiapersonoversikt.infrastructure.kabac.impl.EvaluationContextImpl
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail

object KabacTestUtils {
    fun createTestPolicy(block: (ctx: Kabac.EvaluationContext) -> Decision) = object : Kabac.Policy {
        override val key = Key<Kabac.Policy>("test-policy")
        override fun evaluate(ctx: Kabac.EvaluationContext) = block(ctx)
    }

    class PolicyTester(private val policy: Kabac.Policy) {
        fun assertPermit(vararg attributes: Kabac.PolicyInformationPoint<*>) {
            val ctx = EvaluationContextImpl(*attributes)
            val decision = try {
                ctx.report(policy.key.name).indent()
                policy.evaluate(ctx)
            } catch (e: Throwable) {
                fail("Policy evaluation should not throw exception", e)
            } finally {
                ctx.unindent()
            }
            assertThat(decision::class).isEqualTo(Decision.Permit::class)
            assertThat(decision).isEqualTo(Decision.Permit())
        }

        fun assertDeny(vararg attributes: Kabac.PolicyInformationPoint<*>): MessageAsserter {
            val ctx = EvaluationContextImpl(*attributes)
            val decision = try {
                ctx.report(policy.key.name).indent()
                policy.evaluate(ctx)
            } catch (e: Throwable) {
                fail("Policy evaluation should not throw exception", e)
            } finally {
                ctx.unindent()
            }
            assertThat(decision::class).isEqualTo(Decision.Deny::class)
            return MessageAsserter(decision)
        }

        fun assertNotApplicable(vararg attributes: Kabac.PolicyInformationPoint<*>): MessageAsserter {
            val ctx = EvaluationContextImpl(*attributes)
            val decision = try {
                policy.evaluate(ctx)
            } catch (e: Throwable) {
                fail("Policy evaluation should not throw exception", e)
            }
            assertThat(decision::class).isEqualTo(Decision.NotApplicable::class)
            return MessageAsserter(decision)
        }

        class MessageAsserter(private val decision: Decision) {
            fun withMessage(expectedMessage: String) {
                val actualMessage = when (decision) {
                    is Decision.Deny -> decision.message
                    is Decision.NotApplicable -> decision.message
                    else -> null
                }
                assertThat(actualMessage).isEqualTo(expectedMessage)
            }
        }
    }
}
