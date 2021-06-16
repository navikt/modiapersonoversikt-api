package no.nav.modiapersonoversikt.service.oppgavebehandling

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

data class Response(val total: Long, val data: List<String>)
internal class UtilsTest {
    @Test
    fun `skal ikke havne i evig loop`() {
        Utils.paginering<Response, String>(
            total = { it.total },
            data = { it.data },
            action = { offset ->
                Response(total = 1, data = emptyList())
            }
        )

        assertTrue(true)
    }
}
