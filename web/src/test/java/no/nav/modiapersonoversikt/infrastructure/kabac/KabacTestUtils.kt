package no.nav.modiapersonoversikt.infrastructure.kabac

import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail

object KabacTestUtils {
    class PolicyTester(private val policy: Kabac.Policy) {
        fun assertPermit(vararg attributes: Kabac.AttributeProvider<*>) {
            val ctx = EvaluationContext(*attributes)
            val decision = try {
                policy.evaluate(ctx)
            } catch (e: Throwable) {
                fail("Policy evaluation should not throw exception", e)
            }
            assertThat(decision::class).isEqualTo(Kabac.Decision.Permit::class)
            assertThat(decision).isEqualTo(Kabac.Decision.Permit())
        }

        fun assertDeny(vararg attributes: Kabac.AttributeProvider<*>): MessageAsserter {
            val ctx = EvaluationContext(*attributes)
            val decision = try {
                policy.evaluate(ctx)
            } catch (e: Throwable) {
                fail("Policy evaluation should not throw exception", e)
            }
            assertThat(decision::class).isEqualTo(Kabac.Decision.Deny::class)
            return MessageAsserter(decision)
        }

        fun assertNotApplicable(vararg attributes: Kabac.AttributeProvider<*>): MessageAsserter {
            val ctx = EvaluationContext(*attributes)
            val decision = try {
                policy.evaluate(ctx)
            } catch (e: Throwable) {
                fail("Policy evaluation should not throw exception", e)
            }
            assertThat(decision::class).isEqualTo(Kabac.Decision.NotApplicable::class)
            return MessageAsserter(decision)
        }

        class MessageAsserter(private val decision: Kabac.Decision) {
            fun withMessage(expectedMessage: String) {
                val actualMessage = when (decision) {
                    is Kabac.Decision.Deny, is Kabac.Decision.NotApplicable -> decision.message
                    else -> null
                }
                assertThat(actualMessage).isEqualTo(expectedMessage)
            }
        }
    }
}
