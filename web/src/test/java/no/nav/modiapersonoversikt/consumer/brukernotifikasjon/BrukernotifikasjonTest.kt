package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BrukernotifikasjonTest {
    @Test
    fun `skal bygge opp varslingstidspunkt korrekt`() {
        val event = Brukernotifikasjon.Event(
            fodselsnummer = "10108000398",
            grupperingsId = "1",
            eventId = "2",
            forstBehandlet = ZonedDateTime.parse("2023-01-01T00:00:00.000Z"),
            produsent = "sf-brukernotifikasjon",
            sistOppdatert = ZonedDateTime.parse("2023-08-11T11:11:11.000Z"),
            tekst = "Et nytt samtalereferat er tilgjengelig i din innboks",
            link = "https://innboks.nav.no",
            aktiv = false,
            eksternVarslingSendt = true,
            eksternVarslingKanaler = listOf("SMS", "EPOST"),
            sikkerhetsnivaa = 4,
            eksternVarsling = Brukernotifikasjon.EksternVarslingInfo(
                sendt = true,
                renotifikasjonSendt = true,
                sendteKanaler = listOf("SMS", "EPOST"),
                prefererteKanaler = listOf("SMS", "EPOST"),
                historikk = listOf(
                    Brukernotifikasjon.HistorikkEntry(
                        melding = "Feil telefonummer",
                        status = "feilet",
                        distribusjonsId = 1,
                        kanal = "SMS",
                        renotifikasjon = false,
                        tidspunkt = ZonedDateTime.parse("2023-01-01T00:00:00.000Z")
                    ),
                    Brukernotifikasjon.HistorikkEntry(
                        melding = "Et nytt samtalereferat er tilgjengelig i din innboks",
                        status = "sendt",
                        distribusjonsId = 1,
                        kanal = "EPOST",
                        renotifikasjon = false,
                        tidspunkt = ZonedDateTime.parse("2023-01-11T11:11:11.000Z")
                    ),
                    Brukernotifikasjon.HistorikkEntry(
                        melding = "Et nytt samtalereferat er tilgjengelig i din innboks",
                        status = "sendt",
                        distribusjonsId = 1,
                        kanal = "SMS",
                        renotifikasjon = true,
                        tidspunkt = ZonedDateTime.parse("2023-08-01T00:00:00.000Z")
                    ),
                    Brukernotifikasjon.HistorikkEntry(
                        melding = "Feil epost",
                        status = "feilet",
                        distribusjonsId = 1,
                        kanal = "EPOST",
                        renotifikasjon = true,
                        tidspunkt = ZonedDateTime.parse("2023-08-11T11:11:11.000Z")
                    ),
                )
            )
        )

        val mappedEvent = Brukernotifikasjon.Mapper.byggVarslingsTidspunkt(event)

        assertTrue(mappedEvent.varslingsTidspunkt!!.sendt)
        assertEquals(event.eksternVarsling!!.historikk[1].tidspunkt, mappedEvent.varslingsTidspunkt!!.tidspunkt)
        assertEquals(
            event.eksternVarsling!!.historikk[2].tidspunkt,
            mappedEvent.varslingsTidspunkt!!.renotifikasjonTidspunkt
        )
        assertEquals(listOf("SMS", "EPOST"), mappedEvent.eksternVarslingKanaler)
        assertTrue(mappedEvent.varslingsTidspunkt!!.harFeilteVarslinger)
        assertTrue(mappedEvent.varslingsTidspunkt!!.harFeilteRevarslinger)
        assertEquals("Feil telefonummer", mappedEvent.varslingsTidspunkt!!.feilteVarsliner.first().feilmelding)
        assertEquals("Feil epost", mappedEvent.varslingsTidspunkt!!.feilteRevarslinger.first().feilmelding)
    }
}
