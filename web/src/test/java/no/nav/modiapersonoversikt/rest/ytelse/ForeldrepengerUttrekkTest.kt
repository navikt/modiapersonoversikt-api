package no.nav.modiapersonoversikt.rest.ytelse

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.infrastructure.core.exception.AuthorizationException
import no.nav.modiapersonoversikt.integration.sykmeldingsperioder.consumer.foreldrepenger.DefaultForeldrepengerService
import no.nav.modiapersonoversikt.integration.sykmeldingsperioder.consumer.foreldrepenger.mapping.ForeldrepengerMapper
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.ForeldrepengerV2
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.HentForeldrepengerettighetSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.informasjon.FimFoedsel
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.xml.datatype.DatatypeFactory
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private const val FNR = "10108000398"

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

        assertFailsWith<AuthorizationException> { uttrekk.hent(FNR) }
    }

    @Test
    fun `Test på om felter blir satt`() {
        every { foreldrepengerServiceV2.hentForeldrepengerettighet(any()) } returns mockResponse()

        val response = uttrekk.hent(FNR)
        val foreldrepengerListe = response.get("foreldrepenger") as List<*>
        val foreldrepenger = foreldrepengerListe[0] as Map<String, Any?>

        assertEquals(10, foreldrepenger.get("antallBarn"))
    }

    @Test
    fun `Tester datosetting`() {
        every { foreldrepengerServiceV2.hentForeldrepengerettighet(any()) } returns mockResponse()

        val response = uttrekk.hent(FNR)
        val foreldrepengerListe = response.get("foreldrepenger") as List<*>
        val foreldrepenger = foreldrepengerListe[0] as Map<String, Any?>

        assertEquals("2000-02-01", foreldrepenger.get("barnetsFødselsdato"))
    }

    private fun createXMLGregorianCalendar() = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(2000, 2, 1, 1)

    private fun mockResponse() = FimHentForeldrepengerettighetResponse()
        .withForeldrepengerettighet(
            FimFoedsel()
                .withAntallBarn(BigInteger.TEN)
                .withBarnetFoedt(createXMLGregorianCalendar())
        )
}
