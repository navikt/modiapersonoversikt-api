package no.nav.sbl.dialogarena.modiabrukerdialog.web.rsbac

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import java.lang.RuntimeException

internal class RSBACTest {
    @Test
    fun `should deny if denyCondition is true`() {
        val rsbac = RSBACImpl(null)

        assertThrows<RSBACException> {
            rsbac
                    .deny("") { true }
                    .get { "OK" }
        }

        assertThrows<RSBACException> {
            rsbac
                    .deny("") { true }
                    .get { "OK" }
        }
    }

    @Test
    fun `should deny if permitCondition is false`() {
        val rsbac = RSBACImpl(null)

        assertThrows<RSBACException> {
            rsbac
                    .permit("") { false }
                    .get { "OK" }
        }

        assertThrows<RSBACException> {
            rsbac
                    .permit("") { false }
                    .get { "OK" }
        }
    }

    @Test
    fun `should return message from failed rule`() {
        val rsbac = RSBACImpl(null)

        val failOnFirst: () -> Unit = {
            rsbac
                    .permit("Error 1") { false }
                    .permit("Error 2") { false }
                    .get { "OK" }
        }
        assertThrowsMessage<RSBACException>("Error 1", failOnFirst)

        val failOnLast: () -> Unit = {
            rsbac
                    .permit("Error 1") { true }
                    .permit("Error 2") { false }
                    .get { "OK" }
        }

        assertThrowsMessage<RSBACException>("Error 2", failOnLast)
    }

    @Test
    fun `should return result if every test pass`() {
        val rsbac = RSBACImpl(null)

        val result = rsbac
                .permit("Error 1") { true }
                .deny("Error 2") { false }
                .permit("Error 3") { true }
                .deny("Error 4") { false }
                .get { "OK" }

        assertEquals("OK", result)
    }

    @Test
    fun `should expose context to all rules`() {
        val rsbac = RSBACImpl("Value")

        val result = rsbac
                .permit("Error 1") { context: String -> context == "Value" }
                .permit("Error 2") { context: String -> context == "Value" }
                .deny("Error 3") { context: String -> context != "Value" }
                .get { "OK" }

        assertEquals("OK", result)
    }

    @Test
    fun `should have default deny bias`() {
        val rsbac = RSBACImpl("value")

        val biased: () -> Unit = {
            rsbac
                    .check(Policy("I have no Idea") { DecisionEnums.NOT_APPLICABLE })
                    .get { "OK" }
        }

        assertThrowsMessage<RSBACException>("No matching rule found", biased)
    }

    @Test
    fun `should respect bias`() {
        val rsbac = RSBACImpl("value")

        val biased = rsbac
                .bias(DecisionEnums.PERMIT)
                .check(Policy("I have no Idea") { DecisionEnums.NOT_APPLICABLE })
                .get { "OK" }

        assertEquals("OK", biased)
    }

    @Test
    fun `should return bias if exceptions are caught in policies`() {
        val rsbac = RSBACImpl("value")
        val denyBiased = rsbac
                .bias(DecisionEnums.DENY)
                .check(Policy("I have no Idea") { throw RuntimeException("An error", IllegalStateException("Not allowed...")) })
                .getDecision()
        val permitBiased = rsbac
                .bias(DecisionEnums.PERMIT)
                .check(Policy("I have no Idea") { throw RuntimeException("An error", IllegalStateException("Not allowed...")) })
                .getDecision()

        assertEquals(DecisionEnums.DENY, denyBiased.decision)
        assertEquals(DecisionEnums.PERMIT, permitBiased.decision)
    }

    @Test
    fun `should return bias if exceptions are caught in context`() {
        val result = RSBACImpl(ThrowingContext())
                .check(Policy("Its ok") {
                    it.thisThrowsAnException()
                    DecisionEnums.PERMIT
                })
                .getDecision()

        assertEquals(DecisionEnums.DENY, result.decision)
    }

    inline fun <reified T : Throwable> assertThrowsMessage(expected: String, noinline executable: () -> Unit) =
            assertEquals(expected, assertThrows<T>(executable).message)

    class ThrowingContext {
        fun thisThrowsAnException() {
            throw IllegalStateException("Something went wrong")
        }
    }
}