package no.nav.kjerneinfo.consumer.fim.person.vergemal

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.PdlVerge
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import org.junit.jupiter.api.Test
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPersonVergemaalEllerFullmakt
import org.junit.jupiter.api.DisplayName
import kotlin.test.assertEquals

private const val OMFANG_KODEREF = "personligeInteresser"
private const val TYPE_KODEREF = "voksen"
private const val EMBETE_KODEREF = "fylkesmannenIHedmark"
private const val VERGES_IDENT = "123"
private const val VERGES_NAVN = "Arne"
private const val PDL_VERGES_FNR_MANGLENDE_DATA = "00000000000"

class PdlVergemalServiceTest {
    private val pdl: PdlOppslagService = mock()

    private val pdlVergemalService: PdlVergemalService = PdlVergemalService(pdl)

    @Test
    fun `For personer uten vergemal`() {
        whenever(pdl.hentPersonVergemaalEllerFullmakt(any()))
                .thenReturn(emptyList())
        val vergemal: List<PdlVerge> = pdlVergemalService.hentVergemal(VERGES_IDENT)

        assertEquals(0, vergemal.size)
    }

    @DisplayName("Med vergemål")
    @Test
    fun `Henter informasjon om verge fra PDL`() {
        whenever(pdl.hentPersonVergemaalEllerFullmakt(any())).thenReturn(listOf(getVergeMock(VERGES_IDENT)))
        val vergemal: List<PdlVerge> = pdlVergemalService.hentVergemal(VERGES_IDENT)
        val verge: PdlVerge = vergemal[0]

        assertEquals(VERGES_IDENT, verge.getIdent())
        assertEquals(VERGES_NAVN, verge.getPersonnavn()?.fornavn)


    }

    @DisplayName("Med vergemål med ufullstendig data")
    @Test
    fun `Verges fodselsnummer satt til 0`() {
        whenever(pdl.hentPersonVergemaalEllerFullmakt(any())).thenReturn(listOf(getVergeMockManglendeData(PDL_VERGES_FNR_MANGLENDE_DATA)))
        val vergemal: List<PdlVerge> = pdlVergemalService.hentVergemal(PDL_VERGES_FNR_MANGLENDE_DATA)
        val verge: PdlVerge = vergemal[0]

        assertEquals(null, verge.getIdent())
        assertEquals("", verge.getPersonnavn()?.fornavn)
    }

    private fun getVergeMock(ident: String): HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt {
        return HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt(
                type = TYPE_KODEREF,
                embete = EMBETE_KODEREF,
                vergeEllerFullmektig = HentPersonVergemaalEllerFullmakt.VergeEllerFullmektig(
                        navn = HentPersonVergemaalEllerFullmakt.Personnavn(
                                fornavn = "Arne",
                                mellomnavn = null,
                                etternavn = ""
                        ),
                        motpartsPersonident = ident,
                        omfang = OMFANG_KODEREF,
                        omfangetErInnenPersonligOmraade = false

                ),
                folkeregistermetadata = HentPersonVergemaalEllerFullmakt.Folkeregistermetadata(
                        ajourholdstidspunkt = null,
                        gyldighetstidspunkt = null,
                        opphoerstidspunkt = null,
                        kilde = null,
                        aarsak = null,
                        sekvens = null
                ),
                metadata = HentPersonVergemaalEllerFullmakt.Metadata(
                        opplysningsId = null
                )
        )
    }

    private fun getVergeMockManglendeData(ident: String): HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt {
        return HentPersonVergemaalEllerFullmakt.VergemaalEllerFremtidsfullmakt(
                type = null,
                embete = null,
                vergeEllerFullmektig = HentPersonVergemaalEllerFullmakt.VergeEllerFullmektig(
                        navn = HentPersonVergemaalEllerFullmakt.Personnavn(
                                fornavn = "",
                                mellomnavn = null,
                                etternavn = ""
                        ),
                        motpartsPersonident = null,
                        omfang = null,
                        omfangetErInnenPersonligOmraade = false

                ),
                folkeregistermetadata = HentPersonVergemaalEllerFullmakt.Folkeregistermetadata(
                        ajourholdstidspunkt = null,
                        gyldighetstidspunkt = null,
                        opphoerstidspunkt = null,
                        kilde = null,
                        aarsak = null,
                        sekvens = null
                ),
                metadata = HentPersonVergemaalEllerFullmakt.Metadata(
                        opplysningsId = null
                )
        )
    }
}