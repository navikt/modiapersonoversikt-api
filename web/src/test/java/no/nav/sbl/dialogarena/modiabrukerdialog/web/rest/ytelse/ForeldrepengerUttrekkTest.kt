package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.ytelse

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import no.nav.modig.core.exception.AuthorizationException
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.DefaultForeldrepengerService
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.ForeldrepengerMapper
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeRequest
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeResponse
import no.nav.sykmeldingsperioder.domain.Bruker
import no.nav.sykmeldingsperioder.domain.foreldrepenger.Foreldrepengerettighet
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.ForeldrepengerV2
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.HentForeldrepengerettighetSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.informasjon.FimFoedsel
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.informasjon.FimForeldrepengerettighet
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetResponse
import org.joda.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private const val FNR = "10108000398"

internal class ForeldrepengerUttrekkTest {

    private val foreldrepengerServiceV2: ForeldrepengerV2 = mock()

    private val service = DefaultForeldrepengerService()

    private val uttrekk = ForeldrepengerUttrekk(service)

    @BeforeEach
    fun before() {
        service.setForeldrepengerService(foreldrepengerServiceV2)
        service.setMapper(ForeldrepengerMapper.getInstance())
    }

    @Test
    fun `Kaster Auth exception`() {
        whenever(foreldrepengerServiceV2.hentForeldrepengerettighet(any())).thenThrow(HentForeldrepengerettighetSikkerhetsbegrensning())

        assertFailsWith<AuthorizationException> { uttrekk.hent(FNR) }
    }

    @Test
    fun `Test på om felter blir satt`() {
        whenever(foreldrepengerServiceV2.hentForeldrepengerettighet(any())).thenReturn(mockResponse())

        val response = uttrekk.hent(FNR)
        val foreldrepengerListe = response.get("foreldrepenger") as List<*>
        val foreldrepenger = foreldrepengerListe[0] as Map<String, Any?>

        assertEquals(10, foreldrepenger.get("antallBarn"))
    }

    @Test
    fun `Tester datosetting`() {
        whenever(foreldrepengerServiceV2.hentForeldrepengerettighet(any())).thenReturn(mockResponse())

        val response = uttrekk.hent(FNR)
        val foreldrepengerListe = response.get("foreldrepenger") as List<*>
        val foreldrepenger = foreldrepengerListe[0] as Map<String, Any?>

        assertEquals("2000-02-01", foreldrepenger.get("barnetsFødselsdato"))
    }

    private fun createXMLGregorianCalendar() = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(2000, 2, 1, 1)

    private fun mockResponse() = FimHentForeldrepengerettighetResponse()
            .withForeldrepengerettighet(FimFoedsel()
                    .withAntallBarn(BigInteger.TEN)
                    .withBarnetFoedt(createXMLGregorianCalendar()))
}