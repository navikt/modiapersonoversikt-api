package no.nav.modiapersonoversikt.rest.ytelse

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.infotrygd.foreldrepenger.DefaultForeldrepengerService
import no.nav.modiapersonoversikt.consumer.infotrygd.foreldrepenger.mapping.ForeldrepengerMapper
import no.nav.modiapersonoversikt.infotrgd.foreldrepenger.Foreldrepenger
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.ForeldrepengerV2
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.HentForeldrepengerettighetSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.informasjon.FimFoedsel
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetResponse
import org.joda.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.xml.datatype.DatatypeFactory
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private const val FNR = "10108000398"
private val from = LocalDate.now().minusMonths(2)
private val to = LocalDate.now()

internal class ForeldrepengerUttrekkTest {
    private val foreldrepengerServiceV2: ForeldrepengerV2 = mockk()

    private val service = DefaultForeldrepengerService()

    private val uttrekk = ForeldrepengerUttrekk(service)

    @BeforeEach
    fun before() {
        service.setForeldrepengerService(foreldrepengerServiceV2)
        service.setMapper(ForeldrepengerMapper.getInstance())
    }

    @Test
    fun `Kaster Auth exception`() {
        every { foreldrepengerServiceV2.hentForeldrepengerettighet(any()) } throws HentForeldrepengerettighetSikkerhetsbegrensning()

        assertFailsWith<RuntimeException> { uttrekk.hent(FNR, from, to) }
    }

    @Test
    fun `Test på om felter blir satt`() {
        every { foreldrepengerServiceV2.hentForeldrepengerettighet(any()) } returns mockResponse()

        val foreldrepenger = unwrapResponse()

        assertEquals(10, foreldrepenger?.antallBarn)
    }

    @Test
    fun `Tester datosetting`() {
        every { foreldrepengerServiceV2.hentForeldrepengerettighet(any()) } returns mockResponse()

        val foreldrepenger = unwrapResponse()

        assertEquals("2000-02-01", foreldrepenger?.barnetsFodselsdato)
    }

    private fun unwrapResponse(): Foreldrepenger? = uttrekk.hent(FNR, from, to).foreldrepenger?.get(0)

    private fun createXMLGregorianCalendar() = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(2000, 2, 1, 1)

    private fun mockResponse() =
        FimHentForeldrepengerettighetResponse()
            .withForeldrepengerettighet(
                FimFoedsel()
                    .withAntallBarn(BigInteger.TEN)
                    .withBarnetFoedt(createXMLGregorianCalendar()),
            )
}
