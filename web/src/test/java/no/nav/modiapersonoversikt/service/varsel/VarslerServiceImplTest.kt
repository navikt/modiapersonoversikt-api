package no.nav.modiapersonoversikt.service.varsel

import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.brukernotifikasjon.Brukernotifikasjon
import no.nav.tjeneste.virksomhet.brukervarsel.v1.BrukervarselV1
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSBrukervarsel
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSVarselbestilling
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.WSHentVarselForBrukerResponse
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.xml.soap.SOAPFault
import javax.xml.ws.soap.SOAPFaultException

class VarslerServiceImplTest {
    val clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault())
    val brukervarselV1 = mockk<BrukervarselV1>()
    val brukernotifikasjonService = mockk<Brukernotifikasjon.Service>()
    val soapfault = mockk<SOAPFault>()

    val varselService: VarslerService = VarslerServiceImpl(brukervarselV1, brukernotifikasjonService)

    @Test
    internal fun `skal ikke tryne hele verden om det skjer soap faults`() {
        every { soapfault.faultString } returns ""
        every { brukervarselV1.hentVarselForBruker(any()) } throws SOAPFaultException(soapfault)
        val varsler = varselService.hentLegacyVarsler(Fnr("12345678910"))
        Assertions.assertThat(varsler).isEmpty()
    }

    @Test
    internal fun `skal rapportere om feil i system`() {
        every { soapfault.faultString } returns ""
        every { brukervarselV1.hentVarselForBruker(any()) } throws SOAPFaultException(soapfault)
        every { brukernotifikasjonService.hentAlleBrukernotifikasjoner(any()) } throws IllegalStateException("Noe feil")

        val result = varselService.hentAlleVarsler(Fnr("12345678910"))

        Assertions.assertThat(result.varsler).isEmpty()
        Assertions.assertThat(result.feil).hasSize(2)
    }

    @Test
    internal fun `skal hente varsler fra brukervarsel og brukernotifikasjon`() {
        every { brukervarselV1.hentVarselForBruker(any()) } returns WSHentVarselForBrukerResponse().withBrukervarsel(
            WSBrukervarsel().withVarselbestillingListe(
                WSVarselbestilling(),
                WSVarselbestilling(),
                WSVarselbestilling(),
            )
        )
        every { brukernotifikasjonService.hentAlleBrukernotifikasjoner(any()) } returns listOf(
            event.copy(eventId = "1"),
            event.copy(eventId = "2"),
            event.copy(eventId = "3"),
        )

        val varsler = varselService.hentAlleVarsler(Fnr("12345678910"))
        Assertions.assertThat(varsler.varsler).hasSize(6)
        Assertions.assertThat(varsler.feil).isEmpty()
    }

    private val event = Brukernotifikasjon.Event(
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
        eksternVarslingKanaler = emptyList()
    )
}