package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet

import com.nhaarman.mockito_kotlin.mock
import no.finn.unleash.Unleash
import no.finn.unleash.repository.ToggleFetcher
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.disableFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.enableFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash.UnleashServiceImpl
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

    private val toggleFetcher: ToggleFetcher = mock()
    private val unleash: Unleash = mock()
    private val api = "www.unleashurl.com"
    private var unleashService: UnleashService = UnleashServiceImpl(toggleFetcher, unleash, api)

    @BeforeEach
    fun before() {
        MockitoAnnotations.initMocks(this)

        `when`<Boolean>(unleash!!.isEnabled(Feature.NYTT_VISITTKORT_UNLEASH.propertyKey)).thenReturn(true)
        controller = EnhetController(organisasjonEnhetKontaktinformasjonService, organisasjonEnhetV2Service, unleashService)
    }

    @AfterEach
    fun after() = disableToggle()

    fun disableToggle() {
        `when`<Boolean>(unleash!!.isEnabled(Feature.NYTT_VISITTKORT_UNLEASH.propertyKey)).thenReturn(false)
    }

    @Test
    @DisplayName("Kaster 404 hvis enhet ikke ble funnet")
    fun kaster404HvisEnhetIkkeFunnet() {
        `when`(organisasjonEnhetV2Service.finnNAVKontor(Mockito.any(), Mockito.any())).thenReturn(Optional.empty())
        Assertions.assertThrows(NotFoundException::class.java, { controller.finnEnhet("", "") })
    }

}