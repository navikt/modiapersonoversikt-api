package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.modiapersonoversikt.consumer.tms.generated.BrukervarselDTO
import no.nav.modiapersonoversikt.consumer.tms.generated.EksternVarslingDTO
import no.nav.modiapersonoversikt.consumer.tms.generated.EksternVarslingFeilDTO
import no.nav.modiapersonoversikt.consumer.tms.generated.InnholdDTO
import no.nav.modiapersonoversikt.consumer.tms.generated.ProdusentDTO
import no.nav.modiapersonoversikt.consumer.tms.generated.SensitivitetDTO
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils.objectMapper
import no.nav.modiapersonoversikt.service.varsel.VarslerService
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BrukernotifikasjonTest {
    @Test
    fun `skal mappe opp nytt Varsel-objekt korrekt fra Tms DTO`() {
        val event = getEvent()
        val mappedEvent = Brukernotifikasjon.Mapper.lagVarselFraEvent(event)

        assertEquals(VarslerService.Varsel::class.java, mappedEvent.javaClass)
        assertEquals(event.type.value.uppercase(), mappedEvent.type)
        assertEquals(event.varselId, mappedEvent.varselId)
        assertEquals(event.aktiv, mappedEvent.aktiv)
        assertEquals(event.produsent.appnavn, mappedEvent.produsent)
        assertEquals(event.sensitivitet.value, mappedEvent.sensitivitet)
        assertEquals(event.innhold.tekst, mappedEvent.innhold.tekst)
        assertEquals(event.innhold.link, mappedEvent.innhold.link)
        assertEquals(event.opprettet, mappedEvent.opprettet)
        assertEquals(event.eksternVarsling?.sendt, mappedEvent.eksternVarsling.sendt)
        assertEquals(event.eksternVarsling?.sendtTidspunkt, mappedEvent.eksternVarsling.sendtTidspunkt)
        assertEquals(event.eksternVarsling?.renotifikasjonSendt, mappedEvent.eksternVarsling.renotifikasjonSendt)
        assertEquals(event.eksternVarsling?.renotifikasjonTidspunkt, mappedEvent.eksternVarsling.renotifikasjonTidspunkt)
        assertEquals(event.eksternVarsling?.kanaler, mappedEvent.eksternVarsling.sendteKanaler)
        assertEquals(event.eksternVarsling?.feilhistorikk?.size, mappedEvent.eksternVarsling.feilhistorikk.size)
        assertEquals(event.eksternVarsling?.kanal, mappedEvent.eksternVarsling.kanal)
        assertEquals(event.eksternVarsling?.tittel, mappedEvent.eksternVarsling.tittel)
        assertEquals(event.eksternVarsling?.tekst, mappedEvent.eksternVarsling.tekst)
        assertEquals(event.eksternVarsling?.renotifikasjonKanal, mappedEvent.eksternVarsling.renotifikasjonKanal)
        assertEquals(event.eksternVarsling?.renotifikasjonTittel, mappedEvent.eksternVarsling.renotifikasjonTittel)
        assertEquals(event.eksternVarsling?.renotifikasjonTekst, mappedEvent.eksternVarsling.renotifikasjonTekst)
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
    fun `skal opprette eksternVarsling om det mangler i Tms DTO`() {
        val event = getEvent().copy(eksternVarsling = null)
        val mappedEvent = Brukernotifikasjon.Mapper.lagVarselFraEvent(event)
        assertNotNull(mappedEvent.eksternVarsling)
        assertEquals(mappedEvent.eksternVarsling.sendt, false)
        assertEquals(mappedEvent.eksternVarsling.renotifikasjonTidspunkt, null)
    }

    @Test
    fun `skal mappe alle genererte typeverdier til frontendverdiene`() {
        listOf(
            BrukervarselDTO.Type.oppgave,
            BrukervarselDTO.Type.innboks,
            BrukervarselDTO.Type.beskjed,
        ).forEach { type ->
            val mapped = Brukernotifikasjon.Mapper.lagVarselFraEvent(getEvent().copy(type = type))

            assertEquals(type.value.uppercase(), mapped.type)
        }
    }

    @Test
    fun `skal deserialisere og mappe full Tms JSON og serialisere frontendfeltene`() {
        val json =
            """
            [
              {
                "type": "beskjed",
                "varselId": "synthetic-notice-123",
                "aktiv": true,
                "produsent": {
                  "namespace": "synthetic-space",
                  "appnavn": "synthetic-app",
                  "futureProducerField": "ignored"
                },
                "sensitivitet": "substantial",
                "innhold": {
                  "tekst": "Synthetic notification body",
                  "link": "https://example.test/notice",
                  "futureContentField": true
                },
                "opprettet": "2026-01-29T09:03:00Z",
                "aktivFremTil": "2026-02-18T10:00:00Z",
                "inaktivert": null,
                "inaktivertAv": null,
                "eksternVarsling": {
                  "sendt": true,
                  "sendtSomBatch": false,
                  "renotifikasjonSendt": true,
                  "kanaler": ["SMS", "EPOST"],
                  "feilhistorikk": [
                    {
                      "feilmelding": "Synthetic delivery failure",
                      "tidspunkt": "2026-01-31T09:02:00Z",
                      "futureFailureField": "ignored"
                    }
                  ],
                  "sistOppdatert": "2026-02-08T10:00:00Z",
                  "sendtTidspunkt": "2026-01-29T09:03:00Z",
                  "kanal": "SMS",
                  "tittel": "Synthetic initial title",
                  "tekst": "Synthetic initial text",
                  "renotifikasjonTidspunkt": "2026-02-08T10:00:00Z",
                  "renotifikasjonKanal": "EPOST",
                  "renotifikasjonTittel": "Synthetic retry title",
                  "renotifikasjonTekst": "Synthetic retry text",
                  "futureExternalField": "ignored"
                },
                "futureRootField": "ignored"
              }
            ]
            """.trimIndent()

        val event = objectMapper.readValue<List<BrukervarselDTO>>(json).single()
        val mapped = Brukernotifikasjon.Mapper.lagVarselFraEvent(event)

        assertEquals("BESKJED", mapped.type)
        assertEquals("substantial", mapped.sensitivitet)
        assertEquals("synthetic-app", mapped.produsent)
        assertEquals("synthetic-notice-123", mapped.varselId)
        assertEquals(ZonedDateTime.parse("2026-01-29T09:03:00Z"), mapped.opprettet)
        assertEquals(ZonedDateTime.parse("2026-01-29T09:03:00Z"), mapped.eksternVarsling.sendtTidspunkt)
        assertEquals(ZonedDateTime.parse("2026-02-08T10:00:00Z"), mapped.eksternVarsling.sistOppdatert)
        assertEquals(
            "Synthetic delivery failure",
            mapped.eksternVarsling.feilhistorikk
                .single()
                .feilmelding,
        )
        assertEquals(
            ZonedDateTime.parse("2026-01-31T09:02:00Z"),
            mapped.eksternVarsling.feilhistorikk
                .single()
                .tidspunkt,
        )
        assertEquals("SMS", mapped.eksternVarsling.kanal)
        assertEquals("Synthetic initial title", mapped.eksternVarsling.tittel)
        assertEquals("Synthetic initial text", mapped.eksternVarsling.tekst)
        assertEquals("EPOST", mapped.eksternVarsling.renotifikasjonKanal)
        assertEquals("Synthetic retry title", mapped.eksternVarsling.renotifikasjonTittel)
        assertEquals("Synthetic retry text", mapped.eksternVarsling.renotifikasjonTekst)

        val serialized = objectMapper.readTree(objectMapper.writeValueAsString(mapped))
        val serializedExternal = serialized["eksternVarsling"]
        assertEquals("SMS", serializedExternal["kanal"].asText())
        assertEquals("Synthetic initial title", serializedExternal["tittel"].asText())
        assertEquals("Synthetic initial text", serializedExternal["tekst"].asText())
        assertEquals("EPOST", serializedExternal["renotifikasjonKanal"].asText())
        assertEquals("Synthetic retry title", serializedExternal["renotifikasjonTittel"].asText())
        assertEquals("Synthetic retry text", serializedExternal["renotifikasjonTekst"].asText())
        assertEquals("BESKJED", serialized["type"].asText())
        assertEquals("substantial", serialized["sensitivitet"].asText())
        assertEquals("Synthetic delivery failure", serializedExternal["feilhistorikk"][0]["feilmelding"].asText())
        assertEquals("2026-01-31T09:02:00Z", serializedExternal["feilhistorikk"][0]["tidspunkt"].asText())
    }

    @Test
    fun `skal støtte eldre JSON uten nye felter og eksplisitte nullverdier`() {
        val olderJson =
            """
            [
              {
                "type": "innboks",
                "varselId": "synthetic-old-notice",
                "aktiv": true,
                "produsent": {"namespace": "synthetic-space", "appnavn": "synthetic-app"},
                "sensitivitet": "high",
                "innhold": {"tekst": "Older synthetic message"},
                "opprettet": "2026-02-01T10:00:00Z",
                "eksternVarsling": {
                  "sendt": true,
                  "sendtSomBatch": false,
                  "renotifikasjonSendt": false,
                  "kanaler": ["SMS"],
                  "feilhistorikk": [],
                  "sistOppdatert": "2026-02-01T10:00:00Z"
                }
              }
            ]
            """.trimIndent()
        val explicitNullJson =
            """
            [
              {
                "type": "oppgave",
                "varselId": "synthetic-null-notice",
                "aktiv": true,
                "produsent": {"namespace": "synthetic-space", "appnavn": "synthetic-app"},
                "sensitivitet": "high",
                "innhold": {"tekst": "Synthetic null message"},
                "opprettet": "2026-02-01T10:00:00Z",
                "eksternVarsling": {
                  "sendt": true,
                  "sendtSomBatch": false,
                  "renotifikasjonSendt": false,
                  "kanaler": [],
                  "feilhistorikk": [],
                  "sistOppdatert": "2026-02-01T10:00:00Z",
                  "kanal": null,
                  "tittel": null,
                  "tekst": null,
                  "renotifikasjonKanal": null,
                  "renotifikasjonTittel": null,
                  "renotifikasjonTekst": null
                }
              }
            ]
            """.trimIndent()

        listOf(olderJson, explicitNullJson).forEach { json ->
            val event = objectMapper.readValue<List<BrukervarselDTO>>(json).single()
            val mapped = Brukernotifikasjon.Mapper.lagVarselFraEvent(event)

            assertEquals(null, mapped.eksternVarsling.kanal)
            assertEquals(null, mapped.eksternVarsling.tittel)
            assertEquals(null, mapped.eksternVarsling.tekst)
            assertEquals(null, mapped.eksternVarsling.renotifikasjonKanal)
            assertEquals(null, mapped.eksternVarsling.renotifikasjonTittel)
            assertEquals(null, mapped.eksternVarsling.renotifikasjonTekst)
        }
    }

    @Test
    fun `skal mappe eksternVarsling null i JSON med eksisterende standardverdier`() {
        val json =
            """
            [
              {
                "type": "oppgave",
                "varselId": "synthetic-no-external",
                "aktiv": true,
                "produsent": {"namespace": "synthetic-space", "appnavn": "synthetic-app"},
                "sensitivitet": "high",
                "innhold": {"tekst": "Synthetic message without external notice"},
                "opprettet": "2026-02-01T10:00:00Z",
                "eksternVarsling": null
              }
            ]
            """.trimIndent()

        val event = objectMapper.readValue<List<BrukervarselDTO>>(json).single()
        val mapped = Brukernotifikasjon.Mapper.lagVarselFraEvent(event)

        assertEquals(false, mapped.eksternVarsling.sendt)
        assertEquals(event.opprettet, mapped.eksternVarsling.sendtTidspunkt)
        assertEquals(false, mapped.eksternVarsling.renotifikasjonSendt)
        assertEquals(null, mapped.eksternVarsling.renotifikasjonTidspunkt)
        assertEquals(emptyList(), mapped.eksternVarsling.sendteKanaler)
        assertEquals(emptyList(), mapped.eksternVarsling.feilhistorikk)
        assertEquals(event.opprettet, mapped.eksternVarsling.sistOppdatert)
        assertEquals(null, mapped.eksternVarsling.kanal)
        assertEquals(null, mapped.eksternVarsling.renotifikasjonTekst)
    }

    private fun getEvent(): BrukervarselDTO =
        BrukervarselDTO(
            type = BrukervarselDTO.Type.oppgave,
            varselId = "synthetic-12345",
            aktiv = true,
            produsent =
                ProdusentDTO(
                    namespace = "synthetic-space",
                    appnavn = "synthetic-app",
                ),
            sensitivitet = SensitivitetDTO.high,
            innhold =
                InnholdDTO(
                    tekst = "Synthetic notification text",
                    link = "https://example.test/notice",
                ),
            eksternVarsling =
                EksternVarslingDTO(
                    sendt = true,
                    sendtTidspunkt = ZonedDateTime.parse("2026-01-29T09:03:00.000Z"),
                    sendtSomBatch = false,
                    renotifikasjonSendt = true,
                    renotifikasjonTidspunkt = ZonedDateTime.parse("2026-02-08T10:00:00.000Z"),
                    kanaler = listOf("SMS", "EPOST"),
                    feilhistorikk =
                        listOf(
                            EksternVarslingFeilDTO(
                                feilmelding = "Synthetic delivery failure",
                                tidspunkt = ZonedDateTime.parse("2026-01-31T09:02:00.000Z"),
                            ),
                            EksternVarslingFeilDTO(
                                feilmelding = "Another synthetic delivery failure",
                                tidspunkt = ZonedDateTime.parse("2026-02-06T09:01:11.000Z"),
                            ),
                        ),
                    sistOppdatert = ZonedDateTime.parse("2026-02-08T10:00:00.000Z"),
                    kanal = "SMS",
                    tittel = "Synthetic initial title",
                    tekst = "Synthetic initial text",
                    renotifikasjonKanal = "EPOST",
                    renotifikasjonTittel = "Synthetic retry title",
                    renotifikasjonTekst = "Synthetic retry text",
                ),
            opprettet = ZonedDateTime.parse("2026-01-29T09:03:00.000Z"),
            aktivFremTil = ZonedDateTime.parse("2026-02-18T10:00:00.000Z"),
            inaktivert = null,
            inaktivertAv = null,
        )
}
