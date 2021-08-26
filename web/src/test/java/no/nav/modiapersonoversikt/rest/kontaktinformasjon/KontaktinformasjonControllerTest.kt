package no.nav.modiapersonoversikt.rest.kontaktinformasjon

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.service.dkif.Dkif
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.time.LocalDate
import java.util.*

private const val FNR = "10108000398"
private const val EPOST = "test@testesen.com"
private val MOBILTELEFON = "12345678"
private val SIST_OPPDATERT = LocalDate.of(2012, 12, 27)
private const val RESERVASJON = "Reservert"

class KontaktinformasjonControllerTest {

    private val dkifService: Dkif.Service = mockk()
    private val controller = KontaktinformasjonController(dkifService, TilgangskontrollMock.get())

    @BeforeEach
    fun before() {
        setupDKIFMock()
    }

    private fun setupDKIFMock() {
        every { dkifService.hentDigitalKontaktinformasjon(FNR) } returns Dkif.DigitalKontaktinformasjon(
            personident = null,
            reservasjon = RESERVASJON,
            epostadresse = Dkif.Epostadresse(
                value = EPOST,
                sistOppdatert = SIST_OPPDATERT
            ),
            mobiltelefonnummer = Dkif.MobilTelefon(
                value = MOBILTELEFON,
                sistOppdatert = SIST_OPPDATERT
            )
        )
    }

    @Test
    fun `Henter informasjon fra Digital Kontaktinformasjon registeret`() {
        val response = controller.hentKontaktinformasjon(FNR)
        val epost = response["epost"] as Map<String, String>
        val mobiltelefon = response["mobiltelefon"] as Map<String, String>

        assertAll(
            "Henter epost",
            Executable { assertEquals(EPOST, epost["value"]) },
            Executable { assertEquals(SIST_OPPDATERT, epost["sistOppdatert"]) }
        )

        assertAll(
            "Henter mobiltelefon",
            Executable { assertEquals(MOBILTELEFON, mobiltelefon["value"]) },
            Executable { assertEquals(SIST_OPPDATERT, mobiltelefon["sistOppdatert"]) }
        )

        assertEquals(RESERVASJON, response["reservasjon"])
    }

    @Test
    fun `Når bruker ikke har epost eller mobil`() {
        every { dkifService.hentDigitalKontaktinformasjon(FNR) } returns Dkif.DigitalKontaktinformasjon()

        val response = controller.hentKontaktinformasjon(FNR)
        val epost = response["epost"]
        val mobiltelefon = response["mobiltelefon"]

        assertEquals(null, epost)
        assertEquals(null, mobiltelefon)
    }
}
