package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.vergemal

import no.nav.kjerneinfo.consumer.fim.person.vergemal.VergemalService
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.Periode
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.Verge
import no.nav.kjerneinfo.domain.person.Personnavn
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.disableFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.enableFeature
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Matchers.anyString
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

    @BeforeEach
    fun before() {
        MockitoAnnotations.initMocks(this)
        controller = VergemalController(vergemalService)
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

    companion object {

        @BeforeAll
        @JvmStatic
        fun beforeAll() = enableFeature(Feature.PERSON_REST_API)

        @AfterAll
        @JvmStatic
        fun afterAll() = disableFeature(Feature.PERSON_REST_API)

    }

}
