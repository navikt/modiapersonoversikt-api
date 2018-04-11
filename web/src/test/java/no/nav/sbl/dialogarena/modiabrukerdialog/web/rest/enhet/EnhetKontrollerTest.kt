package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet

import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.disableFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.enableFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.OrganisasjonEnhetV2ServiceImpl
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonServiceImpl
import org.junit.jupiter.api.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.*
import javax.ws.rs.NotFoundException

class EnhetKontrollerTest {
    @Mock private lateinit var oe2service: OrganisasjonEnhetV2ServiceImpl
    @Mock private lateinit var oekservice: OrganisasjonEnhetKontaktinformasjonServiceImpl
    private lateinit var controller: EnhetController

    @BeforeEach
    fun before() {
        MockitoAnnotations.initMocks(this)
        controller = EnhetController(oekservice, oe2service)
    }

    @Test
    @DisplayName("Kaster 404 hvis enhet ikke ble funnet")
    fun kaster404HvisEnhetIkkeFunnet() {
        `when`(oe2service.finnNAVKontor(Mockito.any(), Mockito.any())).thenReturn(Optional.empty())
        Assertions.assertThrows(NotFoundException::class.java, { controller.hentMedGeoTilk("", "") })
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll() = enableFeature(Feature.ENHETER_GEOGRAFISK_TILKNYTNING_API)

        @AfterAll
        @JvmStatic
        fun afterAll() = disableFeature(Feature.ENHETER_GEOGRAFISK_TILKNYTNING_API)
    }

}