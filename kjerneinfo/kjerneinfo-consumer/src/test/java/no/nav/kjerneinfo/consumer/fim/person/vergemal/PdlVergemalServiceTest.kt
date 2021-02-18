package no.nav.kjerneinfo.consumer.fim.person.vergemal

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.PdlVerge
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentNavnBolk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import org.junit.jupiter.api.Test
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPersonVergemaalEllerFullmakt
import kotlin.test.assertEquals

private const val OMFANG_KODEREF = "personligeInteresser"
private const val TYPE_KODEREF = "voksen"
private const val EMBETE_KODEREF = "fylkesmannenIHedmark"
private const val VERGES_IDENT = "123"
private const val VERGES_NAVN = "Arne"

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

    @Test
    fun `Henter informasjon om verge fra PDL`() {
        whenever(pdl.hentPersonVergemaalEllerFullmakt(any())).thenReturn(listOf(getVergeMock(VERGES_IDENT)))
        whenever(pdl.hentNavnBolk(any())).thenReturn(mockPersonnavnForVerge())
        val vergemal: List<PdlVerge> = pdlVergemalService.hentVergemal(VERGES_IDENT)
        val verge: PdlVerge = vergemal[0]

        assertEquals(VERGES_IDENT, verge.ident)
        assertEquals(VERGES_NAVN, verge.personnavn?.fornavn)
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
                        gyldighetstidspunkt = null,
                        opphoerstidspunkt = null
                ),
                metadata = HentPersonVergemaalEllerFullmakt.Metadata(
                        opplysningsId = null
                )
        )
    }


    private fun mockPersonnavnForVerge(): Map<String, HentNavnBolk.Navn?>? {
        return mapOf(Pair(VERGES_IDENT, HentNavnBolk.Navn(VERGES_NAVN, null, "") ))
    }
}