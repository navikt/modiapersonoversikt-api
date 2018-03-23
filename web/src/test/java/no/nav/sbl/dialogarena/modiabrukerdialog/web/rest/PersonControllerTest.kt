package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint
import no.nav.kjerneinfo.consumer.fim.person.support.DefaultPersonKjerneinfoService
import no.nav.kjerneinfo.consumer.fim.person.support.KjerneinfoMapper
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature.PERSON_REST_API
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.FeatureToggle
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person.PersonController
import no.nav.tjeneste.virksomhet.person.v3.HentPersonPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.HentPersonSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.person.v3.PersonV3
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations.initMocks
import javax.ws.rs.NotAuthorizedException
import javax.ws.rs.NotFoundException

private const val FNR = "10108000398"

class PersonControllerTest {

    @Mock private lateinit var mapper: KjerneinfoMapper
    @Mock private lateinit var personV3: PersonV3
    @Mock private lateinit var pep: EnforcementPoint
    @Mock private lateinit var organisasjonenhetV2Service: OrganisasjonEnhetV2Service
    private lateinit var service: DefaultPersonKjerneinfoService
    private lateinit var controller: PersonController

    @BeforeEach
    fun before() {
        initMocks(this)
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

    companion object {

        @BeforeAll @JvmStatic
        fun beforeAll() {
            FeatureToggle.toggleFeature(PERSON_REST_API)
        }

        @AfterAll @JvmStatic
        fun afterAll() {
            FeatureToggle.disableFeature(PERSON_REST_API)
        }

    }

}
