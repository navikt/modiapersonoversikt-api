package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet

import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.disableFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.enableFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonService
import org.junit.jupiter.api.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.*
import javax.ws.rs.NotFoundException

class EnhetControllerTest {
    @Mock private lateinit var organisasjonEnhetV2Service: OrganisasjonEnhetV2Service
    @Mock private lateinit var organisasjonEnhetKontaktinformasjonService: OrganisasjonEnhetKontaktinformasjonService
    private lateinit var controller: EnhetController

    @BeforeEach
    fun before() {
        MockitoAnnotations.initMocks(this)
        controller = EnhetController(organisasjonEnhetKontaktinformasjonService, organisasjonEnhetV2Service)
    }

    @Test
    @DisplayName("Kaster 404 hvis enhet ikke ble funnet")
    fun kaster404HvisEnhetIkkeFunnet() {
        `when`(organisasjonEnhetV2Service.finnNAVKontor(Mockito.any(), Mockito.any())).thenReturn(Optional.empty())
        Assertions.assertThrows(NotFoundException::class.java, { controller.hentMedGeoTilk("", "") })
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