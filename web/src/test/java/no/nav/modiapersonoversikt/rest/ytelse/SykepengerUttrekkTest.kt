package no.nav.modiapersonoversikt.rest.ytelse

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.sykepenger.DefaultSykepengerService
import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.sykepenger.mapping.SykepengerMapper
import no.nav.tjeneste.virksomhet.sykepenger.v2.HentSykepengerListeSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.sykepenger.v2.SykepengerV2
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.FimsykArbeidskategori
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.FimsykBruker
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.FimsykStansaarsak
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.FimsykSykmeldingsperiode
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.xml.datatype.DatatypeFactory
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private const val FNR = "10108000398"
private const val STANS = "STANS"

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
    fun`Kaster Auth exception`() {
        every { sykepengerV2.hentSykepengerListe(any()) } throws HentSykepengerListeSikkerhetsbegrensning()

        assertFailsWith<RuntimeException> { uttrekk.hent(FNR) }
    }

    @Test
    fun`Test på om felter blir satt`() {
        every { sykepengerV2.hentSykepengerListe(any()) } returns mockResponse()

        val sykmeldingsperiode = unwrapResponse()

        assertEquals(STANS, sykmeldingsperiode["stansårsak"])
    }

    @Test
    fun `Riktig dato formattering`() {
        every { sykepengerV2.hentSykepengerListe(any()) } returns mockResponse()

        val sykmeldingsperiode = unwrapResponse()

        assertEquals("2000-02-01", sykmeldingsperiode["sykmeldtFom"])
    }

    private fun unwrapResponse(): Map<String, Any?> {
        val response = uttrekk.hent(FNR)
        val perioderListe = response["sykepenger"] as List<*>
        return perioderListe[0] as Map<String, Any?>
    }

    private fun mockResponse(): FimHentSykepengerListeResponse {
        val periode =
            FimsykSykmeldingsperiode()
                .withStansaarsak(
                    FimsykStansaarsak()
                        .withTermnavn(STANS),
                )
                .withSykmeldtFom(createXMLGregorianCalendar())
                .withArbeidskategori(FimsykArbeidskategori())
        periode.sykmeldt = FimsykBruker().withIdent(FNR)

        return FimHentSykepengerListeResponse().withSykmeldingsperiodeListe(periode)
    }

    private fun createXMLGregorianCalendar() = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(2000, 2, 1, 1)
}
