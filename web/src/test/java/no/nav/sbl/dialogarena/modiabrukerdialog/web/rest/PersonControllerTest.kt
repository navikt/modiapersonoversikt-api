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

private const val FNR = "10108000398"

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
    @DisplayName("Kaster 401 hvis innlogget saksbehandler ikke har tilgang til personen")
    fun kaster401HvisIkkeTilgang() {
        `when`(personV3.hentPerson(Mockito.any())).thenThrow(HentPersonSikkerhetsbegrensning())
        assertThrows(NotAuthorizedException::class.java, { controller.hent(FNR) })
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

    companion object {

        @BeforeAll @JvmStatic
        fun beforeAll() = enableFeature(PERSON_REST_API)

        @AfterAll @JvmStatic
        fun afterAll() = disableFeature(PERSON_REST_API)

    }

}
