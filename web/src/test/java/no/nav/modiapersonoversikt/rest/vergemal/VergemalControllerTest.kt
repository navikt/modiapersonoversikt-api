package no.nav.modiapersonoversikt.rest.vergemal

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentNavnBolk
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.vergemal.VergemalService
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.vergemal.domain.Periode
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.vergemal.domain.Verge
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private const val FNR = "10108000398"
private const val VERGES_IDENT = "123"

class VergemalControllerTest {

    private val vergemalService: VergemalService = mockk()
    private val controller: VergemalController = VergemalController(
        vergemalService,
        TilgangskontrollMock.get()
    )

    @Test
    fun `Henter vergemål`() {
        every { vergemalService.hentVergemal(any()) } returns listOf(
            Verge()
                .withIdent(VERGES_IDENT)
                .withVirkningsperiode(Periode(null, null))
                .withPersonnavn(HentNavnBolk.Navn("", null, ""))
        )

        val response = controller.hent(FNR)

        val verger = response["verger"] as List<*>
        val verge = verger[0] as Map<*, *>

        assertEquals(VERGES_IDENT, verge["ident"])
    }
}
