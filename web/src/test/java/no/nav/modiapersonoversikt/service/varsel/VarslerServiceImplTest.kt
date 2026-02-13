package no.nav.modiapersonoversikt.service.varsel

import io.mockk.every
import io.mockk.mockk
import jakarta.xml.soap.SOAPFault
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.brukernotifikasjon.Brukernotifikasjon
import no.nav.personoversikt.common.logging.TjenestekallLogg
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class VarslerServiceImplTest {
    private val brukernotifikasjonService = mockk<Brukernotifikasjon.Service>()
    private val soapfault = mockk<SOAPFault>()
    private val varselService: VarslerService =
        VarslerServiceImpl(brukernotifikasjonService, TjenestekallLogg)

    @Test
    internal fun `skal rapportere om feil i system`() {
        every { soapfault.faultString } returns ""
        every { brukernotifikasjonService.hentAlleBrukernotifikasjoner(any()) } throws IllegalStateException("Noe feil")

        val result = varselService.hentAlleVarsler(Fnr("12345678910"))

        assertThat(result.varsler).isEmpty()
        assertThat(result.feil).hasSize(1)
    }

    @Test
    internal fun `skal hente varsler fra brukernotifikasjon`() {
        every { brukernotifikasjonService.hentAlleBrukernotifikasjoner(any()) } returns
            listOf(
                varsel.copy(varselId = "1"),
                varsel.copy(varselId = "2"),
                varsel.copy(varselId = "3"),
            )

        val varsler = varselService.hentAlleVarsler(Fnr("12345678910"))
        assertThat(varsler.varsler).hasSize(3)
        assertThat(varsler.feil).isEmpty()
    }

    private val varsel = VarslerService.Varsel(
        type = "OPPGAVE",
        varselId = "12345",
        aktiv = true,
        produsent = "et app-navn",
        sensitivitet = "high",
        innhold = Brukernotifikasjon.Innhold(
            tekst = "Et nytt samtalereferat er tilgjengelig i din innboks",
            link = "https://nav.test.no",
        ),
        eksternVarsling = VarslerService.VarselInfo(
            sendt = true,
            sendtTidspunkt = ZonedDateTime.parse("2026-01-29T09:03:00.000Z"),
            renotifikasjonSendt = true,
            renotifikasjonTidspunkt = ZonedDateTime.parse("2026-02-06T09:03:00.000Z"),
            sendteKanaler = listOf("SMS", "EPOST"),
            feilhistorikk =
                listOf(
                    Brukernotifikasjon.Feilhistorikk(
                        feilmelding = "En feil",
                        tidspunkt = ZonedDateTime.parse("2026-01-31T09:02:00.000Z"),
                    ),
                    Brukernotifikasjon.Feilhistorikk(
                        feilmelding = "Enda en feil",
                        tidspunkt = ZonedDateTime.parse("2026-02-06T09:01:11.000Z"),
                    ),
                ),
            sistOppdatert = ZonedDateTime.parse("2026-02-06T09:03:00.000Z"),
        ),
        opprettet = ZonedDateTime.parse("2026-01-29T09:03:00.000Z"),
    )
}
