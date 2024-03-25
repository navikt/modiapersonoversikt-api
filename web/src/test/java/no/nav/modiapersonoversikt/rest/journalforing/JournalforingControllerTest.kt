package no.nav.modiapersonoversikt.rest.journalforing

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import no.nav.common.utils.fn.UnsafeSupplier
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.journalforingsaker.SakerService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import no.nav.modiapersonoversikt.testutils.AuthContextTestUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.test.assertEquals

internal class JournalforingControllerTest {
    val ident = "Z999643"
    val sak =
        JournalforingSak().apply {
            temaKode = "DAG"
            fagsystemKode = "IT01"
            fagsystemSaksId = "987654"
        }
    val sakerService: SakerService = mockk()
    val sfHenvendelseService: SfHenvendelseService = mockk()
    val controller =
        JournalforingController(
            sakerService,
            sfHenvendelseService,
            TilgangskontrollMock.get(),
        )

    @Test
    fun `knytter til sak og returnerer 200 OK`() {
        every { sfHenvendelseService.journalforHenvendelse(any(), any(), any(), any(), any()) } just runs

        AuthContextTestUtils.withIdent(
            ident,
            UnsafeSupplier {
                controller.knyttTilSak("10108000398", "traad-id", sak, "1234")
            },
        )
    }

    @Test
    fun `forespørsler som fieler kaster 500 Internal Server Error`() {
        every {
            sfHenvendelseService.journalforHenvendelse(
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } throws IllegalStateException("Something went wrong")

        val exception =
            assertThrows<ResponseStatusException> {
                AuthContextTestUtils.withIdent(
                    ident,
                    UnsafeSupplier {
                        controller.knyttTilSak("10108000398", "traad-id", sak, "1234")
                    },
                )
            }

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.statusCode)
        assertEquals(JournalforingController.UKJENT_FEIL, exception.reason)
    }

    @Test
    fun `forespørsler uten enhet kaster 500 Internal Server Error med message satt i body`() {
        every { sfHenvendelseService.journalforHenvendelse(any(), any(), any(), any(), any()) } just runs

        val exception =
            assertThrows<ResponseStatusException> {
                AuthContextTestUtils.withIdent(
                    ident,
                    UnsafeSupplier {
                        controller.knyttTilSak("10108000398", "traad-id", sak, null)
                    },
                )
            }

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.statusCode)
        assertEquals(JournalforingController.FEILMELDING_UTEN_ENHET, exception.reason)
    }
}
