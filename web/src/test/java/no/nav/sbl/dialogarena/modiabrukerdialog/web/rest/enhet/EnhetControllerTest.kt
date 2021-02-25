package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollMock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*
import kotlin.test.assertEquals

class EnhetControllerTest {
    private val organisasjonEnhetV2Service: OrganisasjonEnhetV2Service = mock()
    private val controller = EnhetController(
        mock(),
        organisasjonEnhetV2Service,
        mock(),
        mock(),
        TilgangskontrollMock.get()
    )

    @Test
    fun `Kaster 404 hvis enhet ikke ble funnet`() {
        whenever(organisasjonEnhetV2Service.finnNAVKontor(Mockito.any(), Mockito.any())).thenReturn(Optional.empty())
        val exception = assertThrows<ResponseStatusException> {
            controller.finnEnhet("", "")
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.status)
    }
}
