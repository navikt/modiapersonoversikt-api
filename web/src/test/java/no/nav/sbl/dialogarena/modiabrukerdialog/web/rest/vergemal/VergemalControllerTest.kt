package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.vergemal

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import no.nav.kjerneinfo.consumer.fim.person.vergemal.VergemalService
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.Periode
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.Verge
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentNavnBolk
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollMock
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

private const val FNR = "10108000398"
private const val VERGES_IDENT = "123"

class VergemalControllerTest {

    private val vergemalService: VergemalService = mock()
    private val controller: VergemalController = VergemalController(
            vergemalService,
            TilgangskontrollMock.get()
    )


    @Test
    fun `Henter vergemål`() {
        whenever(vergemalService.hentVergemal(any())).thenReturn(Arrays.asList(Verge()
                .withIdent(VERGES_IDENT)
                .withVirkningsperiode(Periode(null, null))
                .withPersonnavn(HentNavnBolk.Navn("", null, ""))))

        val response = controller.hent(FNR)

        val verger = response["verger"] as List<*>
        val verge = verger[0] as Map<*, *>

        assertEquals(VERGES_IDENT, verge["ident"])
    }
}
