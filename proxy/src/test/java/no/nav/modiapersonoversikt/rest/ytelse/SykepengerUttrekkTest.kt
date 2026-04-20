package no.nav.modiapersonoversikt.rest.ytelse

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.infotrygd.sykepenger.DefaultSykepengerService
import no.nav.modiapersonoversikt.consumer.infotrygd.sykepenger.SykepengerServiceBi
import no.nav.modiapersonoversikt.consumer.infotrygd.sykepenger.mapping.SykepengerMapper
import no.nav.modiapersonoversikt.consumer.infotrygd.sykepenger.mapping.to.SykepengerRequest
import no.nav.modiapersonoversikt.infotrgd.sykepenger.Sykepenger
import no.nav.tjeneste.virksomhet.sykepenger.v2.HentSykepengerListeSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.sykepenger.v2.SykepengerV2
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.FimsykArbeidskategori
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.FimsykBruker
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.FimsykStansaarsak
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.FimsykSykmeldingsperiode
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeResponse
import org.joda.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.xml.datatype.DatatypeFactory
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private const val FNR = "10108000398"
private const val STANS = "STANS"
private val from = LocalDate.now().minusMonths(2)
private val to = LocalDate.now()

internal class SykepengerUttrekkTest {
    private val sykepengerV2: SykepengerV2 = mockk()

    private val service = DefaultSykepengerService()

    private val uttrekk = SykepengerUttrekk(service)

    @BeforeEach
    fun before() {
        service.setSykepengerService(sykepengerV2)
        service.setMapper(SykepengerMapper.getInstance())
    }

    @Test
    fun `Kaster Auth exception`() {
        every { sykepengerV2.hentSykepengerListe(any()) } throws HentSykepengerListeSikkerhetsbegrensning()

        assertFailsWith<RuntimeException> { uttrekk.hent(FNR, from, to) }
    }

    @Test
    fun `Test på om felter blir satt`() {
        every { sykepengerV2.hentSykepengerListe(any()) } returns mockResponse()

        val sykmeldingsperiode = unwrapResponse()

        assertEquals(STANS, sykmeldingsperiode?.stansaarsak)
    }

    @Test
    fun `Riktig dato formattering`() {
        every { sykepengerV2.hentSykepengerListe(any()) } returns mockResponse()

        val sykmeldingsperiode = unwrapResponse()

        assertEquals("2000-02-01", sykmeldingsperiode?.sykmeldtFom)
    }

    private fun unwrapResponse(): Sykepenger? = uttrekk.hent(FNR, from, to).sykepenger?.get(0)

    private fun mockResponse(): FimHentSykepengerListeResponse {
        val periode =
            FimsykSykmeldingsperiode()
                .withStansaarsak(
                    FimsykStansaarsak()
                        .withTermnavn(STANS),
                ).withSykmeldtFom(createXMLGregorianCalendar())
                .withArbeidskategori(FimsykArbeidskategori())
        periode.sykmeldt = FimsykBruker().withIdent(FNR)

        return FimHentSykepengerListeResponse().withSykmeldingsperiodeListe(periode)
    }

    private fun createXMLGregorianCalendar() = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(2000, 2, 1, 1)
}

internal class SykepengerUttrekkSplitTest {
    private val mockService: SykepengerServiceBi = mockk()
    private val uttrekk = SykepengerUttrekk(mockService)
    private val capturedRequests = mutableListOf<SykepengerRequest>()

    @BeforeEach
    fun before() {
        capturedRequests.clear()
        every { mockService.hentSykmeldingsperioder(any()) } answers {
            capturedRequests.add(firstArg())
            null
        }
    }

    @Test
    fun `Periode under to år gir ett kall`() {
        val from = LocalDate.now().minusYears(1)
        val to = LocalDate.now()

        uttrekk.hent(FNR, from, to)

        assertEquals(1, capturedRequests.size)
        assertEquals(from, capturedRequests[0].from)
        assertEquals(to, capturedRequests[0].to)
    }

    @Test
    fun `Periode på nøyaktig to år gir ett kall`() {
        val from = LocalDate(2020, 1, 1)
        val to = from.plusYears(2)

        uttrekk.hent(FNR, from, to)

        assertEquals(1, capturedRequests.size)
        assertEquals(from, capturedRequests[0].from)
        assertEquals(to, capturedRequests[0].to)
    }

    @Test
    fun `Periode over to år deles i flere kall`() {
        val from = LocalDate(2020, 1, 1)
        val to = LocalDate(2023, 6, 1)

        uttrekk.hent(FNR, from, to)

        assertEquals(2, capturedRequests.size)
        assertEquals(from, capturedRequests[0].from)
        assertEquals(from.plusYears(2), capturedRequests[0].to)
        assertEquals(from.plusYears(2), capturedRequests[1].from)
        assertEquals(to, capturedRequests[1].to)
    }

    @Test
    fun `Periode over fire år deles i tre kall`() {
        val from = LocalDate(2018, 1, 1)
        val to = LocalDate(2023, 1, 2)

        uttrekk.hent(FNR, from, to)

        assertEquals(3, capturedRequests.size)
        assertEquals(from, capturedRequests[0].from)
        assertEquals(from.plusYears(2), capturedRequests[1].from)
        assertEquals(from.plusYears(4), capturedRequests[2].from)
        assertEquals(to, capturedRequests[2].to)
    }

    @Test
    fun `Fra og til samme dag gir ett kall`() {
        val date = LocalDate(2022, 6, 15)

        uttrekk.hent(FNR, date, date)

        assertEquals(1, capturedRequests.size)
        assertEquals(date, capturedRequests[0].from)
        assertEquals(date, capturedRequests[0].to)
    }
}
