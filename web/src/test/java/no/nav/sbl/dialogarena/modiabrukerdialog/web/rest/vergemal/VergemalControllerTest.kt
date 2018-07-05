package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.vergemal

import com.nhaarman.mockito_kotlin.mock
import no.finn.unleash.Unleash
import no.finn.unleash.repository.ToggleFetcher
import no.nav.kjerneinfo.consumer.fim.person.vergemal.VergemalService
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.Periode
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.Verge
import no.nav.kjerneinfo.domain.person.Personnavn
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.disableFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.enableFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash.UnleashServiceImpl
import org.junit.jupiter.api.*
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.*
import kotlin.test.assertEquals

private const val FNR = "10108000398"
private const val VERGES_IDENT = "123"

class VergemalControllerTest {

    @Mock
    private lateinit var vergemalService: VergemalService

    private lateinit var controller: VergemalController

    private val toggleFetcher: ToggleFetcher = mock()
    private val unleash: Unleash = mock()
    private val api = "www.unleashurl.com"
    private var unleashService: UnleashService = UnleashServiceImpl(toggleFetcher, unleash, api)

    @BeforeEach
    fun before() {
        MockitoAnnotations.initMocks(this)

        `when`<Boolean>(unleash!!.isEnabled(Feature.NYTT_VISITTKORT_UNLEASH.propertyKey)).thenReturn(true)

        controller = VergemalController(vergemalService, unleashService)
    }

    @AfterEach
    fun after() = disableToggle()

    fun disableToggle() {
        `when`<Boolean>(unleash!!.isEnabled(Feature.NYTT_VISITTKORT_UNLEASH.propertyKey)).thenReturn(false)
    }

    @Test
    fun henterVergemal() {
        `when`(vergemalService.hentVergemal(anyString())).thenReturn(Arrays.asList(Verge()
                .withIdent(VERGES_IDENT)
                .withVirkningsperiode(Periode(null, null))
                .withPersonnavn(Personnavn())))

        val response = controller.hent(FNR)

        val verger = response["verger"] as List<*>
        val verge = verger[0] as Map<*, *>

        assertEquals(VERGES_IDENT, verge["ident"])
    }
}
