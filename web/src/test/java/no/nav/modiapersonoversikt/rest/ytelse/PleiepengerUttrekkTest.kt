package no.nav.modiapersonoversikt.rest.ytelse

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.infrastructure.core.exception.AuthorizationException
import no.nav.modiapersonoversikt.integration.sykmeldingsperioder.consumer.pleiepenger.PleiepengerServiceImpl
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.organisasjon.OrganisasjonService
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

    private val pleiepengerV1: PleiepengerV1 = mockk()
    private val service = PleiepengerServiceImpl(pleiepengerV1)

    private val org: OrganisasjonV4 = mockk()
    private val orgService: OrganisasjonService = mockk()

    private val uttrekk = PleiepengerUttrekk(service, orgService)

    @Test
    fun `Kaster Auth exception`() {
        every { pleiepengerV1.hentPleiepengerettighet(any()) } throws HentPleiepengerettighetSikkerhetsbegrensning()

        assertFailsWith<AuthorizationException> { uttrekk.hent(FNR) }
    }

    @Test
    fun `Tom liste returnerer null`() {
        every { pleiepengerV1.hentPleiepengerettighet(any()) } returns WSHentPleiepengerettighetResponse()

        val response = uttrekk.hent(FNR)
        val pleiepenger = response.get("pleiepenger")

        assertNull(pleiepenger)
    }

    @Test
    fun `Test på om felter blir satt`() {
        every { pleiepengerV1.hentPleiepengerettighet(any()) } returns mockResponse()

        val response = uttrekk.hent(FNR)
        val pleiepengerListe = response.get("pleiepenger") as List<*>
        val pleiepenger = pleiepengerListe[0] as Map<String, Any?>

        assertEquals(BARNETS_FNR, pleiepenger.get("barnet"))
    }

    private fun mockResponse() = WSHentPleiepengerettighetResponse()
        .withPleiepengerettighetListe(
            WSPleiepengerettighet()
                .withBarnet(
                    WSPerson()
                        .withIdent(BARNETS_FNR)
                )
                .withOmsorgsperson(
                    WSPerson()
                        .withIdent(FNR)
                )
        )
}
