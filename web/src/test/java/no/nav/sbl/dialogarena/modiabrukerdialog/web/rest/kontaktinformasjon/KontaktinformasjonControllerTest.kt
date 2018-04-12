package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.kontaktinformasjon

import no.nav.dkif.consumer.DkifService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.disableFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.enableFeature
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSEpostadresse
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSKontaktinformasjon
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSMobiltelefonnummer
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonResponse
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.function.Executable
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
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

    @Mock
    private lateinit var dkifService: DkifService

    private lateinit var controller: KontaktinformasjonController

    @BeforeEach
    fun before() {
        MockitoAnnotations.initMocks(this)
        setupDKIFMock()
        controller = KontaktinformasjonController(dkifService)
    }

    private fun setupDKIFMock() {
        val epost = WSEpostadresse().withValue(EPOST).withSistOppdatert(lagDato(SIST_OPPDATERT))
        val mobiltelefon = WSMobiltelefonnummer().withValue(MOBILTELEFON).withSistOppdatert(lagDato(SIST_OPPDATERT))

        `when`(dkifService.hentDigitalKontaktinformasjon(FNR))
                .thenReturn(WSHentDigitalKontaktinformasjonResponse()
                        .withDigitalKontaktinformasjon(WSKontaktinformasjon()
                                .withReservasjon(RESERVASJON)
                                .withEpostadresse(epost)
                                .withMobiltelefonnummer(mobiltelefon)))
    }

    @Test
    @DisplayName("Henter informasjon fra Digital Kontaktinformasjon registeret")
    fun henterKontaktinformasjon() {
        val response = controller.hentKontaktinformasjon(FNR)
        val epost = response["epost"] as Map<String, String>
        val mobiltelefon = response["mobiltelefon"] as Map<String, String>

        assertAll("Henter epost",
                Executable { assertEquals(EPOST, epost["value"]) },
                Executable { assertEquals(lagDato(SIST_OPPDATERT), epost["sistOppdatert"])}
        )

        assertAll("Henter mobiltelefon",
                Executable { assertEquals(MOBILTELEFON, mobiltelefon["value"]) },
                Executable { assertEquals(lagDato(SIST_OPPDATERT), mobiltelefon["sistOppdatert"])}
        )

        assertEquals(RESERVASJON, response["reservasjon"])
    }

    @Test
    @DisplayName("Når bruker ikke har epost eller mobil")
    fun brukerUtenEpostOgTelefon() {
        `when`(dkifService.hentDigitalKontaktinformasjon(FNR)).thenReturn(WSHentDigitalKontaktinformasjonResponse()
                .withDigitalKontaktinformasjon(WSKontaktinformasjon()))

        val response = controller.hentKontaktinformasjon(FNR)
        val epost = response["epost"] as Map<String, String>
        val mobiltelefon = response["mobiltelefon"] as Map<String, String>

        assertEquals(null, epost["value"])
        assertEquals(null, mobiltelefon["mobiltelefon"])
        assertEquals(null, mobiltelefon["reservasjon"])
    }

    private fun lagDato(dateSource: String): XMLGregorianCalendar? {
        val date = GregorianCalendar()
        val dateFormatter = SimpleDateFormat("dd.mm.yyyy")
        date.time = dateFormatter.parse(dateSource)

        return DatatypeFactory.newInstance().newXMLGregorianCalendar(date)
    }

    companion object {

        @BeforeAll
        @JvmStatic
        fun beforeAll() = enableFeature(Feature.PERSON_REST_API)

        @AfterAll
        @JvmStatic
        fun afterAll() = disableFeature(Feature.PERSON_REST_API)

    }

}