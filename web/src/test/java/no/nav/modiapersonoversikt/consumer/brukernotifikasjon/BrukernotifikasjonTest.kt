package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import no.nav.modiapersonoversikt.service.varsel.VarslerService
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class BrukernotifikasjonTest {
    @Test
    fun `skal opprette nytt Varsel-objekt ved mottak av event og sette obligatoriske verdier for eldre eventer`() {
        val event =
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
                        sendtSomBatch = null,
                        renotifikasjonSendt = null,
                        renotifikasjonTidspunkt = null,
                        sendteKanaler = listOf("SMS", "EPOST"),
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
                        sistOppdatert = ZonedDateTime.parse("2026-02-06T09:03:00.000Z"),
                    ),
                opprettet = ZonedDateTime.parse("2026-01-29T09:03:00.000Z"),
                aktivFremTil = ZonedDateTime.parse("2026-02-06T09:02:00.000Z"),
                inaktivert = null,
                inaktivertAv = null,
            )

        val mappedEvent = Brukernotifikasjon.Mapper.lagVarselFraEvent(event)

        println(mappedEvent)
        assertEquals(mappedEvent.javaClass, VarslerService.Varsel::class.java)
    }
}
