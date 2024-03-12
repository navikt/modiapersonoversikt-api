package no.nav.modiapersonoversikt.rest.kontaktinformasjon

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.krr.Krr
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.time.LocalDate

private const val FNR = "10108000398"
private const val EPOST = "test@testesen.com"
private const val MOBILTELEFON = "12345678"
private const val RESERVASJON = "Reservert"
private val SIST_OPPDATERT = LocalDate.of(2012, 12, 27)

class KontaktinformasjonControllerTest {
    private val krrService: Krr.Service = mockk()
    private val controller = KontaktinformasjonController(krrService, TilgangskontrollMock.get())

    @BeforeEach
    fun before() {
        setupDKIFMock()
    }

    private fun setupDKIFMock() {
        every { krrService.hentDigitalKontaktinformasjon(FNR) } returns
            Krr.DigitalKontaktinformasjon(
                personident = null,
                reservasjon = RESERVASJON,
                epostadresse =
                    Krr.Epostadresse(
                        value = EPOST,
                        sistOppdatert = SIST_OPPDATERT,
                    ),
                mobiltelefonnummer =
                    Krr.MobilTelefon(
                        value = MOBILTELEFON,
                        sistOppdatert = SIST_OPPDATERT,
                    ),
            )
    }

    @Test
    fun `Henter informasjon fra Digital Kontaktinformasjon registeret`() {
        val response = controller.hentKontaktinformasjon(FNR)
        val epost = response.epost
        val mobiltelefon = response.mobiltelefon

        assertAll(
            "Henter epost",
            Executable { assertEquals(EPOST, epost?.value) },
            Executable { assertEquals(SIST_OPPDATERT, epost?.sistOppdatert) },
        )

        assertAll(
            "Henter mobiltelefon",
            Executable { assertEquals(MOBILTELEFON, mobiltelefon?.value) },
            Executable { assertEquals(SIST_OPPDATERT, mobiltelefon?.sistOppdatert) },
        )

        assertEquals(RESERVASJON, response.reservasjon)
    }

    @Test
    fun `Når bruker ikke har epost eller mobil`() {
        every { krrService.hentDigitalKontaktinformasjon(FNR) } returns Krr.DigitalKontaktinformasjon()

        val response = controller.hentKontaktinformasjon(FNR)
        val epost = response.epost
        val mobiltelefon = response.mobiltelefon

        assertEquals(null, epost)
        assertEquals(null, mobiltelefon)
    }
}
