package no.nav.modiapersonoversikt.utils

import io.mockk.mockk
import no.nav.common.log.MDCConstants
import no.nav.common.utils.fn.UnsafeSupplier
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.testutils.AuthContextTestUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage.withPercentage
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletWebRequest

internal class ConcurrencyUtilsTest {
    @Test
    internal fun `should run tasks in parallel`() {
        val taskA = { Thread.sleep(1000L) }
        val taskB = { Thread.sleep(1000L) }

        val startTime = System.currentTimeMillis()
        ConcurrencyUtils.inParallel(taskA, taskB)
        val endTime = System.currentTimeMillis()

        assertThat(endTime - startTime).isCloseTo(1000, withPercentage(15.0))
    }

    @Test
    internal fun `should copy mdc`() {
        MDC.put(MDCConstants.MDC_CALL_ID, "CallId")
        MDC.put("TestKey", "TestValue")

        val (taskA, taskB) = ConcurrencyUtils.inParallel(
            { getCallId() to MDC.get("TestKey") },
            { getCallId() to MDC.get("TestKey") }
        )

        assertThat(taskA).isEqualTo("CallId" to "TestValue")
        assertThat(taskB).isEqualTo("CallId" to "TestValue")
    }

    @Test
    internal fun `should copy subject`() {
        val (taskA, taskB) = AuthContextTestUtils.withIdent(
            "Z999999",
            UnsafeSupplier {
                ConcurrencyUtils.inParallel(
                    { AuthContextUtils.requireIdent() },
                    { AuthContextUtils.requireIdent() }
                )
            }
        )
        assertThat(taskA).isEqualTo("Z999999")
        assertThat(taskB).isEqualTo("Z999999")
    }

    @Test
    internal fun `should copy requestContext`() {
        val attributes = ServletWebRequest(mockk())
        RequestContextHolder.setRequestAttributes(attributes)
        val (taskA, taskB) = ConcurrencyUtils.inParallel(
            { RequestContextHolder.getRequestAttributes() },
            { RequestContextHolder.getRequestAttributes() }
        )
        RequestContextHolder.setRequestAttributes(null)

        assertThat(taskA).isEqualTo(attributes)
        assertThat(taskB).isEqualTo(attributes)
    }
}
