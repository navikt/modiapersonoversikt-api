package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import no.nav.modiapersonoversikt.service.varsel.VarslerService
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BrukernotifikasjonTest {
    @Test
    fun `skal mappe opp nytt Varsel-objekt korrekt fra Event`() {
        val event = getEvent()
        val mappedEvent = Brukernotifikasjon.Mapper.lagVarselFraEvent(event)

        assertEquals(mappedEvent.javaClass, VarslerService.Varsel::class.java)
        assertEquals(event.type.name, mappedEvent.type)
        assertEquals(event.varselId, mappedEvent.varselId)
        assertEquals(event.aktiv, mappedEvent.aktiv)
        assertEquals(event.produsent.appnavn, mappedEvent.produsent)
        assertEquals(event.sensitivitet, mappedEvent.sensitivitet)
        assertEquals(event.innhold.tekst, mappedEvent.innhold.tekst)
        assertEquals(event.innhold.link, mappedEvent.innhold.link)
        assertEquals(event.opprettet, mappedEvent.opprettet)
        assertEquals(event.eksternVarsling?.sendt, mappedEvent.eksternVarsling.sendt)
        assertEquals(event.eksternVarsling?.sendtTidspunkt, mappedEvent.eksternVarsling.sendtTidspunkt)
        assertEquals(event.eksternVarsling?.renotifikasjonSendt, mappedEvent.eksternVarsling.renotifikasjonSendt)
        assertEquals(event.eksternVarsling?.renotifikasjonTidspunkt, mappedEvent.eksternVarsling.renotifikasjonTidspunkt)
        assertEquals(event.eksternVarsling?.kanaler, mappedEvent.eksternVarsling.sendteKanaler)
        assertEquals(event.eksternVarsling?.feilHistorikk?.size, mappedEvent.eksternVarsling.feilhistorikk.size)
    }

    @Test
    fun `skal sette renotifikasjonSendt til true naar opprettet foer cutover og sist oppdatert ikke er samme dato som opprettet`() {
        val opprettet = ZonedDateTime.parse("2026-01-20T09:00:00.000Z")
        val sistOppdatert = ZonedDateTime.parse("2026-01-25T10:00:00.000Z")
        val event =
            getEvent().copy(
                opprettet = opprettet,
                eksternVarsling = getEvent().eksternVarsling?.copy(sistOppdatert = sistOppdatert),
            )
        val mappedEvent = Brukernotifikasjon.Mapper.lagVarselFraEvent(event)

        assertEquals(true, mappedEvent.eksternVarsling.renotifikasjonSendt)
        assertEquals(sistOppdatert, mappedEvent.eksternVarsling.renotifikasjonTidspunkt)
    }

    @Test
    fun `skal opprette eksternVarsling om det mangler i Event`() {
        val event = getEvent().copy(eksternVarsling = null)
        val mappedEvent = Brukernotifikasjon.Mapper.lagVarselFraEvent(event)
        assertNotNull(mappedEvent.eksternVarsling)
        assertEquals(mappedEvent.eksternVarsling.sendt, false)
        assertEquals(mappedEvent.eksternVarsling.renotifikasjonTidspunkt, null)
    }

    private fun getEvent(): Brukernotifikasjon.Event =
        Brukernotifikasjon.Event(
            type = Brukernotifikasjon.Type.OPPGAVE,
            varselId = "12345",
            aktiv = true,
            produsent =
                Brukernotifikasjon.Produsent(
                    namespace = "test-space",
                    appnavn = "et app-navn",
                ),
            sensitivitet = "high",
            innhold =
                Brukernotifikasjon.Innhold(
                    tekst = "Et nytt samtalereferat er tilgjengelig i din innboks",
                    link = "https://nav.test.no",
                ),
            eksternVarsling =
                Brukernotifikasjon.EksternVarslingInfo(
                    sendt = true,
                    sendtTidspunkt = ZonedDateTime.parse("2026-01-29T09:03:00.000Z"),
                    sendtSomBatch = false,
                    renotifikasjonSendt = true,
                    renotifikasjonTidspunkt = ZonedDateTime.parse("2026-02-08T10:00:00.000Z"),
                    kanaler = listOf("SMS", "EPOST"),
                    feilHistorikk =
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
                    sistOppdatert = ZonedDateTime.parse("2026-02-08T10:00:00.000Z"),
                ),
            opprettet = ZonedDateTime.parse("2026-01-29T09:03:00.000Z"),
            aktivFremTil = ZonedDateTime.parse("2026-02-18T10:00:00.000Z"),
            inaktivert = null,
            inaktivertAv = null,
        )
}
