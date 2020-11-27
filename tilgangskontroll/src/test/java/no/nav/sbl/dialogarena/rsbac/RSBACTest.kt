package no.nav.sbl.dialogarena.rsbac

import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import no.nav.sbl.dialogarena.naudit.Audit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class RSBACTest {
    @Test
    fun `should deny if denyCondition is true`() {
        val rsbac = RSBACImpl(null)

        assertThrows<RSBACException> {
            rsbac
                    .deny("") { true }
                    .get(Audit.skipAuditLog) { "OK" }
        }

        assertThrows<RSBACException> {
            rsbac
                    .deny("") { true }
                    .get(Audit.skipAuditLog) { "OK" }
        }
    }

    @Test
    fun `should deny if permitCondition is false`() {
        val rsbac = RSBACImpl(null)

        assertThrows<RSBACException> {
            rsbac
                    .permit("") { false }
                    .get(Audit.skipAuditLog) { "OK" }
        }

        assertThrows<RSBACException> {
            rsbac
                    .permit("") { false }
                    .get(Audit.skipAuditLog) { "OK" }
        }
    }

    @Test
    fun `should return message from failed rule`() {
        val rsbac = RSBACImpl(null)

        val failOnFirst: () -> Unit = {
            rsbac
                    .permit("Error 1") { false }
                    .permit("Error 2") { false }
                    .get(Audit.skipAuditLog) { "OK" }
        }
        assertThrowsMessage<RSBACException>("Error 1", failOnFirst)

        val failOnLast: () -> Unit = {
            rsbac
                    .permit("Error 1") { true }
                    .permit("Error 2") { false }
                    .get(Audit.skipAuditLog) { "OK" }
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
                .get(Audit.skipAuditLog) { "OK" }

        assertEquals("OK", result)
    }

    @Test
    fun `should expose context to all rules`() {
        val rsbac = RSBACImpl("Value")

        val result = rsbac
                .permit("Error 1") { context: String -> context == "Value" }
                .permit("Error 2") { context: String -> context == "Value" }
                .deny("Error 3") { context: String -> context != "Value" }
                .get(Audit.skipAuditLog) { "OK" }

        assertEquals("OK", result)
    }

    @Test
    fun `should have default deny bias`() {
        val rsbac = RSBACImpl("value")

        val biased: () -> Unit = {
            rsbac
                    .check(Policy("I have no Idea") { DecisionEnums.NOT_APPLICABLE })
                    .get(Audit.skipAuditLog) { "OK" }
        }

        assertThrowsMessage<RSBACException>("No matching rule found", biased)
    }

    @Test
    fun `should respect bias`() {
        val rsbac = RSBACImpl("value")

        val biased = rsbac
                .bias(DecisionEnums.PERMIT)
                .check(Policy("I have no Idea") { DecisionEnums.NOT_APPLICABLE })
                .get(Audit.skipAuditLog) { "OK" }

        assertEquals("OK", biased)
    }

    @Test
    fun `should log audit-descriptor`() {
        val rsbac = RSBACImpl(null)
        val auditDescriptor = mock<Audit.AuditDescriptor<String>>()

        runCatching {
            rsbac
                    .permit("Error 1") { true }
                    .get(auditDescriptor) { "OK" }
        }

        verify(auditDescriptor).log(eq("OK"))
    }

    @Test
    fun `should log deny-audit-descriptor`() {
        val rsbac = RSBACImpl(null)
        val auditDescriptor = mock<Audit.AuditDescriptor<String>>()

        runCatching {
            rsbac
                    .deny("Error 1") { true }
                    .get(auditDescriptor) { "OK" }
        }

        verify(auditDescriptor).denied(eq("Error 1"))
    }

    @Test
    fun `should log failed-audit-descriptor`() {
        val rsbac = RSBACImpl(null)
        val auditDescriptor = mock<Audit.AuditDescriptor<String>>()
        val exception = IllegalStateException("Something wrong")

        val result = runCatching {
            rsbac
                    .permit("Error 1") { true }
                    .get(auditDescriptor) { throw exception }
        }

        assertEquals(result.isFailure, true)
        assertEquals(result.exceptionOrNull(), exception)
        verify(auditDescriptor).failed(eq(exception))
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

        assertEquals(DecisionEnums.DENY, denyBiased.value)
        assertEquals(DecisionEnums.PERMIT, permitBiased.value)
    }

    @Test
    fun `should return bias if exceptions are caught in context`() {
        val result = RSBACImpl(ThrowingContext())
                .check(Policy("Its ok") {
                    thisThrowsAnException()
                    DecisionEnums.PERMIT
                })
                .getDecision()

        assertEquals(DecisionEnums.DENY, result.value)
    }

    inline fun <reified T : Throwable> assertThrowsMessage(expected: String, noinline executable: () -> Unit) =
            assertEquals(expected, assertThrows<T>(executable).message)

    class ThrowingContext {
        fun thisThrowsAnException() {
            throw IllegalStateException("Something went wrong")
        }
    }
}
