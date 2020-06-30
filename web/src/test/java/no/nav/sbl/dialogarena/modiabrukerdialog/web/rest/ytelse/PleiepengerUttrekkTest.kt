package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.ytelse

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import no.nav.kjerneinfo.consumer.organisasjon.OrganisasjonV4ServiceImpl
import no.nav.modig.core.exception.AuthorizationException
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerServiceImpl
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4
import no.nav.tjeneste.virksomhet.pleiepenger.v1.HentPleiepengerettighetSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.pleiepenger.v1.PleiepengerV1
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.WSPerson
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.WSPleiepengerettighet
import no.nav.tjeneste.virksomhet.pleiepenger.v1.meldinger.WSHentPleiepengerettighetResponse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

private const val FNR = "10108000398"
private const val BARNETS_FNR = "01010012345"

internal class PleiepengerUttrekkTest {

    private val pleiepengerV1: PleiepengerV1 = mock()
    private val service = PleiepengerServiceImpl(pleiepengerV1)

    private val org:  OrganisasjonV4 = mock()
    private val orgService = OrganisasjonV4ServiceImpl(org)

    private val uttrekk = PleiepengerUttrekk(service, orgService)

    @Test
    fun`Kaster Auth exception`() {
        whenever(pleiepengerV1.hentPleiepengerettighet(any())).thenThrow(HentPleiepengerettighetSikkerhetsbegrensning())

        assertFailsWith<AuthorizationException> { uttrekk.hent(FNR) }
    }

    @Test
    fun`Tom liste returnerer null`() {
        whenever(pleiepengerV1.hentPleiepengerettighet(any())).thenReturn(WSHentPleiepengerettighetResponse())

        val response = uttrekk.hent(FNR)
        val pleiepenger = response.get("pleiepenger")

        assertNull(pleiepenger)
    }

    @Test
    fun`Test på om felter blir satt`() {
        whenever(pleiepengerV1.hentPleiepengerettighet(any())).thenReturn(mockResponse())

        val response = uttrekk.hent(FNR)
        val pleiepengerListe = response.get("pleiepenger") as List<*>
        val pleiepenger = pleiepengerListe[0] as Map<String, Any?>

        assertEquals(BARNETS_FNR, pleiepenger.get("barnet"))
    }

    private fun mockResponse() = WSHentPleiepengerettighetResponse()
            .withPleiepengerettighetListe(WSPleiepengerettighet()
                    .withBarnet(WSPerson()
                            .withIdent(BARNETS_FNR))
                    .withOmsorgsperson(WSPerson()
                            .withIdent(FNR)))
}