package no.nav.modiapersonoversikt.rest.enhet

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.test.assertEquals

class EnhetControllerTest {
    private val norgApi: NorgApi = mockk()
    private val controller = EnhetController(
        norgApi,
        mockk(),
        mockk(),
        TilgangskontrollMock.get()
    )

    @Test
    fun `Kaster 404 hvis enhet ikke ble funnet`() {
        every { norgApi.finnNavKontor(any(), any()) } throws IllegalStateException("Not found")
        val exception = assertThrows<ResponseStatusException> {
            controller.finnEnhet("", "")
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.status)
    }
}
