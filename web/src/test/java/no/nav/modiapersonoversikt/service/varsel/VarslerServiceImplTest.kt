package no.nav.modiapersonoversikt.service.varsel

import io.mockk.every
import io.mockk.mockk
import jakarta.xml.soap.SOAPFault
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.brukernotifikasjon.Brukernotifikasjon
import no.nav.personoversikt.common.logging.TjenestekallLogg
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class VarslerServiceImplTest {
    private val clock: Clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault())
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
                event.copy(eventId = "1"),
                event.copy(eventId = "2"),
                event.copy(eventId = "3"),
            )

        val varsler = varselService.hentAlleVarsler(Fnr("12345678910"))
        assertThat(varsler.varsler).hasSize(3)
        assertThat(varsler.feil).isEmpty()
    }

    private val event =
        Brukernotifikasjon.Event(
            fodselsnummer = "12345679810",
            grupperingsId = "987",
            eventId = "123",
            forstBehandlet = ZonedDateTime.now(clock),
            produsent = "srvappname",
            sikkerhetsnivaa = 4,
            sistOppdatert = ZonedDateTime.now(clock),
            tekst = "Dette er en tekst",
            link = "http://dummy.io/",
            aktiv = true,
            eksternVarslingSendt = false,
            eksternVarslingKanaler = emptyList(),
        )
}
