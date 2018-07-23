package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.vergemal

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import no.nav.kjerneinfo.consumer.fim.person.vergemal.VergemalService
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.Periode
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.Verge
import no.nav.kjerneinfo.domain.person.Personnavn
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash.UnleashServiceImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

private const val FNR = "10108000398"
private const val VERGES_IDENT = "123"

class VergemalControllerTest {

    private val vergemalService: VergemalService = mock()
    private val unleashService = UnleashServiceImpl(mock(), mock(), "www.unleashurl.com")
    private val controller: VergemalController = VergemalController(
            vergemalService,
            unleashService
    )

    @BeforeEach
    fun before() {
        whenever(unleashService.isEnabled(Feature.NYTT_VISITTKORT)).thenReturn(true)
    }

    @Test
    fun `Henter vergemål`() {
        whenever(vergemalService.hentVergemal(any())).thenReturn(Arrays.asList(Verge()
                .withIdent(VERGES_IDENT)
                .withVirkningsperiode(Periode(null, null))
                .withPersonnavn(Personnavn())))

        val response = controller.hent(FNR)

        val verger = response["verger"] as List<*>
        val verge = verger[0] as Map<*, *>

        assertEquals(VERGES_IDENT, verge["ident"])
    }
}
