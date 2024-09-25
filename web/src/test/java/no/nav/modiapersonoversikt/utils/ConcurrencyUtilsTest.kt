package no.nav.modiapersonoversikt.utils

import io.mockk.mockk
import no.nav.common.log.MDCConstants
import no.nav.common.utils.fn.UnsafeSupplier
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.testutils.AuthContextTestUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletWebRequest

internal class ConcurrencyUtilsTest {
    @Test
    internal fun `should copy mdc`() {
        MDC.put(MDCConstants.MDC_CALL_ID, "CallId")
        MDC.put("TestKey", "TestValue")

        val result =
            ConcurrencyUtils
                .makeThreadSwappable {
                    getCallId() to MDC.get("TestKey")
                }.invoke()

        assertThat(result).isEqualTo("CallId" to "TestValue")
    }

    @Test
    internal fun `should copy subject`() {
        val result =
            AuthContextTestUtils.withIdent(
                "Z999999",
                UnsafeSupplier {
                    ConcurrencyUtils
                        .makeThreadSwappable {
                            AuthContextUtils.requireIdent()
                        }.invoke()
                },
            )
        assertThat(result).isEqualTo("Z999999")
    }

    @Test
    internal fun `should copy requestContext`() {
        val attributes = ServletWebRequest(mockk())
        RequestContextHolder.setRequestAttributes(attributes)
        val result =
            ConcurrencyUtils
                .makeThreadSwappable {
                    RequestContextHolder.getRequestAttributes()
                }.invoke()
        RequestContextHolder.setRequestAttributes(null)

        assertThat(result).isEqualTo(attributes)
    }
}
