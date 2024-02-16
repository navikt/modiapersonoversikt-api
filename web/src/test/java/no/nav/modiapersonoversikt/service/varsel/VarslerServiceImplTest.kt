package no.nav.modiapersonoversikt.service.varsel

import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.brukernotifikasjon.Brukernotifikasjon
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.tjeneste.virksomhet.brukervarsel.v1.BrukervarselV1
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSBrukervarsel
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSVarselbestilling
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.WSHentVarselForBrukerResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.xml.soap.SOAPFault
import javax.xml.ws.soap.SOAPFaultException

class VarslerServiceImplTest {
    private val clock: Clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault())
    private val brukervarselV1 = mockk<BrukervarselV1>()
    private val brukernotifikasjonService = mockk<Brukernotifikasjon.Service>()
    private val soapfault = mockk<SOAPFault>()
    private val unleashService = mockk<UnleashService>()
    private val varselService: VarslerService =
        VarslerServiceImpl(brukervarselV1, brukernotifikasjonService, unleashService)

    @Test
    internal fun `skal ikke tryne hele verden om det skjer soap faults`() {
        every { unleashService.isEnabled(Feature.TMS_EVENT_API_UPDATE.propertyKey) } returns false
        every { soapfault.faultString } returns ""
        every { brukervarselV1.hentVarselForBruker(any()) } throws SOAPFaultException(soapfault)
        val varsler = varselService.hentLegacyVarsler(Fnr("12345678910"))
        assertThat(varsler).isEmpty()
    }

    @Test
    internal fun `skal rapportere om feil i system`() {
        every { unleashService.isEnabled(Feature.TMS_EVENT_API_UPDATE.propertyKey) } returns false
        every { soapfault.faultString } returns ""
        every { brukervarselV1.hentVarselForBruker(any()) } throws SOAPFaultException(soapfault)
        every { brukernotifikasjonService.hentAlleBrukernotifikasjoner(any()) } throws IllegalStateException("Noe feil")

        val result = varselService.hentAlleVarsler(Fnr("12345678910"))

        assertThat(result.varsler).isEmpty()
        assertThat(result.feil).hasSize(2)
    }

    @Test
    internal fun `skal hente varsler fra brukervarsel og brukernotifikasjon`() {
        every { unleashService.isEnabled(Feature.TMS_EVENT_API_UPDATE.propertyKey) } returns false
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
        assertThat(varsler.varsler).hasSize(6)
        assertThat(varsler.feil).isEmpty()
    }

    @Test
    internal fun `skal hente varsler fra brukervarsel og v2 brukernotifikasjon`() {
        every { unleashService.isEnabled(Feature.TMS_EVENT_API_UPDATE.propertyKey) } returns true
        every { brukervarselV1.hentVarselForBruker(any()) } returns WSHentVarselForBrukerResponse().withBrukervarsel(
            WSBrukervarsel().withVarselbestillingListe(
                WSVarselbestilling(),
            )
        )
        every { brukernotifikasjonService.hentAlleBrukernotifikasjonerV2(any()) } returns listOf(
            eventV2.copy(varselId = "1"),
            eventV2.copy(varselId = "2"),
            eventV2.copy(varselId = "3"),
        )

        val varsler = varselService.hentAlleVarsler(Fnr("12345678910"))
        assertThat(varsler.varsler).hasSize(4)
        assertThat(varsler.feil).isEmpty()
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

    private val eventV2 = Brukernotifikasjon.EventV2(
        type = "beskjed",
        varselId = "123",
        aktive = true,
        opprettet = ZonedDateTime.now(clock),
        produsent = Brukernotifikasjon.Produsent("", "srvappname"),
        innhold = Brukernotifikasjon.Innhold("text", "link"),
        aktivFremTil = ZonedDateTime.now(clock),
        inaktivert = ZonedDateTime.now(clock),
        sensitivitet = "substantial",
        inaktivertAv = "http://dummy.io/",
    )
}
