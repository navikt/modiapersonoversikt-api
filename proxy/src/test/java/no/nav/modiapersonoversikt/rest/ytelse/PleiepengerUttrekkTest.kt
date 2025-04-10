package no.nav.modiapersonoversikt.rest.ytelse

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.ereg.OrganisasjonService
import no.nav.modiapersonoversikt.consumer.infotrygd.pleiepenger.PleiepengerServiceImpl
import no.nav.tjeneste.virksomhet.pleiepenger.v1.HentPleiepengerettighetSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.pleiepenger.v1.PleiepengerV1
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.WSPerson
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.WSPleiepengerettighet
import no.nav.tjeneste.virksomhet.pleiepenger.v1.meldinger.WSHentPleiepengerettighetResponse
import org.joda.time.LocalDate
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

private const val FNR = "10108000398"
private const val BARNETS_FNR = "01010012345"
private val from = LocalDate.now().minusMonths(2)
private val to = LocalDate.now()

internal class PleiepengerUttrekkTest {
    private val pleiepengerV1: PleiepengerV1 = mockk()
    private val service = PleiepengerServiceImpl(pleiepengerV1)

    private val orgService: OrganisasjonService = mockk()

    private val uttrekk = PleiepengerUttrekk(service, orgService)

    @Test
    fun `Kaster Auth exception`() {
        every { pleiepengerV1.hentPleiepengerettighet(any()) } throws HentPleiepengerettighetSikkerhetsbegrensning()

        assertFailsWith<RuntimeException> { uttrekk.hent(FNR, from, to) }
    }

    @Test
    fun `Tom liste returnerer null`() {
        every { pleiepengerV1.hentPleiepengerettighet(any()) } returns WSHentPleiepengerettighetResponse()
        val pleiepenger = unwrapResponse()

        assertNull(pleiepenger)
    }

    @Test
    fun `Test på om felter blir satt`() {
        every { pleiepengerV1.hentPleiepengerettighet(any()) } returns mockResponse()
        val pleiepenger = unwrapResponse()

        assertEquals(BARNETS_FNR, pleiepenger?.barnet)
    }

    private fun unwrapResponse(): Pleiepenger? = uttrekk.hent(FNR, from, to).pleiepenger?.get(0)

    private fun mockResponse() =
        WSHentPleiepengerettighetResponse()
            .withPleiepengerettighetListe(
                WSPleiepengerettighet()
                    .withBarnet(
                        WSPerson()
                            .withIdent(BARNETS_FNR),
                    ).withOmsorgsperson(
                        WSPerson()
                            .withIdent(FNR),
                    ),
            )
}
