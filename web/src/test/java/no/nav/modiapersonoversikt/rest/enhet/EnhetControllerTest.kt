package no.nav.modiapersonoversikt.rest.enhet

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.legacy.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*
import kotlin.test.assertEquals

class EnhetControllerTest {
    private val organisasjonEnhetV2Service: OrganisasjonEnhetV2Service = mockk()
    private val controller = EnhetController(
        mockk(),
        organisasjonEnhetV2Service,
        mockk(),
        mockk(),
        TilgangskontrollMock.get()
    )

    @Test
    fun `Kaster 404 hvis enhet ikke ble funnet`() {
        every { organisasjonEnhetV2Service.finnNAVKontor(any(), any()) } returns Optional.empty()
        val exception = assertThrows<ResponseStatusException> {
            controller.finnEnhet("", "")
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.status)
    }
}
