package no.nav.modiapersonoversikt.rest.kontaktinformasjon

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.dkif.consumer.DkifService
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSEpostadresse
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSKontaktinformasjon
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSMobiltelefonnummer
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonResponse
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

private const val FNR = "10108000398"
private const val EPOST = "test@testesen.com"
private const val SIST_OPPDATERT = "27.12.12"
private const val MOBILTELEFON = "27.12.12"
private const val RESERVASJON = "Reservert"

class KontaktinformasjonControllerTest {

    private val dkifService: DkifService = mockk()
    private val controller = KontaktinformasjonController(dkifService, TilgangskontrollMock.get())

    @BeforeEach
    fun before() {
        setupDKIFMock()
    }

    private fun setupDKIFMock() {
        val epost = WSEpostadresse().withValue(EPOST).withSistOppdatert(lagDato(SIST_OPPDATERT))
        val mobiltelefon = WSMobiltelefonnummer().withValue(MOBILTELEFON).withSistOppdatert(lagDato(SIST_OPPDATERT))

        every { dkifService.hentDigitalKontaktinformasjon(FNR) } returns WSHentDigitalKontaktinformasjonResponse()
            .withDigitalKontaktinformasjon(
                WSKontaktinformasjon()
                    .withReservasjon(RESERVASJON)
                    .withEpostadresse(epost)
                    .withMobiltelefonnummer(mobiltelefon)
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
            Executable { assertEquals(lagDato(SIST_OPPDATERT), epost["sistOppdatert"]) }
        )

        assertAll(
            "Henter mobiltelefon",
            Executable { assertEquals(MOBILTELEFON, mobiltelefon["value"]) },
            Executable { assertEquals(lagDato(SIST_OPPDATERT), mobiltelefon["sistOppdatert"]) }
        )

        assertEquals(RESERVASJON, response["reservasjon"])
    }

    @Test
    fun `Når bruker ikke har epost eller mobil`() {
        every { dkifService.hentDigitalKontaktinformasjon(FNR) } returns
            WSHentDigitalKontaktinformasjonResponse()
                .withDigitalKontaktinformasjon(WSKontaktinformasjon())

        val response = controller.hentKontaktinformasjon(FNR)
        val epost = response["epost"]
        val mobiltelefon = response["mobiltelefon"]

        assertEquals(null, epost)
        assertEquals(null, mobiltelefon)
    }

    private fun lagDato(dateSource: String): XMLGregorianCalendar? {
        val date = GregorianCalendar()
        val dateFormatter = SimpleDateFormat("dd.mm.yyyy")
        date.time = dateFormatter.parse(dateSource)

        return DatatypeFactory.newInstance().newXMLGregorianCalendar(date)
    }
}
