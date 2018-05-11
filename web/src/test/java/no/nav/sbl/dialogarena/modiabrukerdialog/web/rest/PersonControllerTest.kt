package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint
import no.nav.kjerneinfo.consumer.fim.person.support.DefaultPersonKjerneinfoService
import no.nav.kjerneinfo.consumer.fim.person.support.KjerneinfoMapper
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature.PERSON_REST_API
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.disableFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.enableFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person.PersonController
import no.nav.tjeneste.virksomhet.person.v3.HentPersonPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.HentPersonSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.person.v3.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentPersonResponse
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentSikkerhetstiltakResponse
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations.initMocks
import java.util.*
import javax.ws.rs.NotAuthorizedException
import javax.ws.rs.NotFoundException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val FNR = "10108000398"
private const val TELEFONNUMMER = "10108000398"

class PersonControllerTest {

    private lateinit var mapper: KjerneinfoMapper
    @Mock private lateinit var personV3: PersonV3
    @Mock private lateinit var pep: EnforcementPoint
    @Mock private lateinit var organisasjonenhetV2Service: OrganisasjonEnhetV2Service
    private lateinit var service: DefaultPersonKjerneinfoService
    private lateinit var controller: PersonController

    @BeforeEach
    fun before() {
        initMocks(this)
        val kodeverkManagerMock = mock(KodeverkmanagerBi::class.java)
        mapper = KjerneinfoMapper(kodeverkManagerMock)
        `when`(organisasjonenhetV2Service.finnNAVKontor(Mockito.any(), Mockito.any())).thenReturn(Optional.empty())

        service = DefaultPersonKjerneinfoService(personV3, mapper, pep, organisasjonenhetV2Service)
        controller = PersonController(service)
    }

    @Test
    @DisplayName("Kaster 404 hvis personen ikke ble funnet")
    fun kaster404HvisPersonIkkeFunnet() {
        `when`(personV3.hentPerson(Mockito.any())).thenThrow(HentPersonPersonIkkeFunnet())
        assertThrows(NotFoundException::class.java, { controller.hent(FNR) })
    }

    @Test
    @DisplayName("Returnerer begrenset innsyn object ved ikke tilgang")
    fun returnBegrensetInnsyn() {
        `when`(personV3.hentPerson(Mockito.any())).thenThrow(HentPersonSikkerhetsbegrensning())
        `when`(personV3.hentSikkerhetstiltak(Mockito.any()))
                .thenReturn(WSHentSikkerhetstiltakResponse()
                        .withSikkerhetstiltak(WSSikkerhetstiltak()
                                .withSikkerhetstiltaksbeskrivelse("")))
        val o = controller.hent(FNR)
        assertTrue { o.containsKey("begrunnelse") }
    }

    @Test
    @DisplayName("Mapper om ukjente statsborgerskap til null")
    fun mapperUkjentStatsborgerskap() {
        val mockPersonResponse = mockPersonResponse()
        mockPersonResponse.person.withStatsborgerskap(WSStatsborgerskap().withLand(WSLandkoder().withValue("???")))
        `when`(personV3.hentPerson(Mockito.any())).thenReturn(mockPersonResponse)

        val response = controller.hent(FNR)
        val statsborgerskap = response["statsborgerskap"]

        assertEquals(null, statsborgerskap)
    }

    private fun mockPersonResponse(): WSHentPersonResponse {
        return WSHentPersonResponse().withPerson(WSBruker()
                .withPersonnavn(WSPersonnavn())
                .withKjoenn(WSKjoenn().withKjoenn(WSKjoennstyper().withValue("K")))
                .withAktoer(WSPersonIdent().withIdent(WSNorskIdent().withIdent(FNR))))
    }

    @Nested
    inner class Kontaktinformasjon {

        @Test
        fun medMobil() {
            val mockPersonResponse = responseMedMobil()
            `when`(personV3.hentPerson(Mockito.any())).thenReturn(mockPersonResponse)

            val response = controller.hent(FNR)
            val kontaktinformasjon = response["kontaktinformasjon"] as Map<*, *>
            val mobil = kontaktinformasjon["mobil"] as Map<*, *>
            val nummer = mobil["nummer"]

            assertEquals(TELEFONNUMMER, nummer)
        }

        private fun responseMedMobil(): WSHentPersonResponse {
            val mockPersonResponse = mockPersonResponse()
            val bruker = mockPersonResponse.person as WSBruker
            bruker.withKontaktinformasjon(WSTelefonnummer()
                    .withIdentifikator(TELEFONNUMMER)
                    .withType(WSTelefontyper().withValue("MOBI")))
            return mockPersonResponse
        }

        @Test
        fun utenMobil() {
            val mockPersonResponse = mockPersonResponse()
            `when`(personV3.hentPerson(Mockito.any())).thenReturn(mockPersonResponse)

            val response = controller.hent(FNR)
            val kontaktinformasjon = response["kontaktinformasjon"] as Map<*, *>
            val mobil = kontaktinformasjon["mobil"]

            assertEquals(null, mobil)
        }

    }

    companion object {

        @BeforeAll @JvmStatic
        fun beforeAll() = enableFeature(PERSON_REST_API)

        @AfterAll @JvmStatic
        fun afterAll() = disableFeature(PERSON_REST_API)

    }

}
