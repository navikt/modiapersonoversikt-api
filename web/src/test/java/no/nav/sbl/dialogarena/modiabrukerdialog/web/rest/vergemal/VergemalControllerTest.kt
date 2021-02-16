package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.vergemal

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import no.nav.kjerneinfo.consumer.fim.person.vergemal.PdlVergemalService
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.PdlVerge
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPersonVergemaalEllerFullmakt
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollMock
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private const val FNR = "10108000398"
private const val VERGES_IDENT = "123"

class VergemalControllerTest {

    private val vergemalService: PdlVergemalService = mock()
    private val controller: VergemalController = VergemalController(
            vergemalService,
            TilgangskontrollMock.get()
    )


    @Test
    fun `Henter vergemål`() {
        whenever(vergemalService.hentVergemal(any())).thenReturn(listOf(PdlVerge(
                ident = VERGES_IDENT,
                personnavn = HentPersonVergemaalEllerFullmakt.Personnavn2(
                        fornavn = "",
                        mellomnavn = null,
                        etternavn = ""
                ),
                embete = null,
                omfang = null,
                vergesakstype = null,
                gyldighetstidspunkt = null,
                opphoerstidspunkt = null
        )))

        val response = controller.hent(FNR)

        val verger = response["verger"] as List<*>
        val verge = verger[0] as Map<*, *>

        assertEquals(VERGES_IDENT, verge["ident"])
    }
}
