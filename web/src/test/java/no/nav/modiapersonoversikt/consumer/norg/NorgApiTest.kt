package no.nav.modiapersonoversikt.consumer.norg

import com.fasterxml.jackson.core.type.TypeReference
import no.nav.modiapersonoversikt.consumer.norg.generated.infrastructure.Serializer
import no.nav.modiapersonoversikt.consumer.norg.generated.models.RsAdresseDTO
import no.nav.modiapersonoversikt.consumer.norg.generated.models.RsEnhetInkludertKontaktinformasjonDTO
import no.nav.modiapersonoversikt.consumer.norg.generated.models.RsPostboksadresseDTO
import no.nav.modiapersonoversikt.consumer.norg.generated.models.RsStedsadresseDTO
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class NorgApiTest {
    private val jackson = Serializer.jacksonObjectMapper

    @Test
    internal fun `skal kunne deserialisere postboksadresse`() {
        val postboksadresseJson = """
            {
                "type": "postboksadresse",
                "postnummer": "5807",
                "poststed": "BERGEN",
                "postboksnummer": "2303",
                "postboksanlegg": "Solheimsviken"
            }
        """.trimIndent()

        val adresse = jackson.readValue(postboksadresseJson, object : TypeReference<RsAdresseDTO>() {})
        assertTrue(adresse is RsPostboksadresseDTO)
    }

    @Test
    internal fun `skal kunne deserialisere stedsadresse`() {
        val stedsadresse = """
            {
                "type": "stedsadresse",
                "postnummer": "5058",
                "poststed": "BERGEN",
                "gatenavn": "Solheimsgaten",
                "husnummer": "13",
                "husbokstav": null,
                "adresseTilleggsnavn": null
            }
        """.trimIndent()

        val adresse = jackson.readValue(stedsadresse, object : TypeReference<RsAdresseDTO>() {})
        assertTrue(adresse is RsStedsadresseDTO)
    }

    @Test
    internal fun `skal kunne mappe komplett kontaktinfo for enhet`() {
        val json = """
             {
                "enhet": {
                  "enhetId": 100000123,
                  "navn": "NAV Enhet",
                  "enhetNr": "1234",
                  "antallRessurser": 0,
                  "status": "Under avvikling",
                  "orgNivaa": "EN",
                  "type": "TILTAK",
                  "organisasjonsnummer": null,
                  "underEtableringDato": "1970-01-01",
                  "aktiveringsdato": "1970-01-01",
                  "underAvviklingDato": null,
                  "nedleggelsesdato": null,
                  "oppgavebehandler": true,
                  "versjon": 8,
                  "sosialeTjenester": "",
                  "kanalstrategi": null,
                  "orgNrTilKommunaltNavKontor": null
                },
                "overordnetEnhet": null,
                "habilitetskontor": null,
                "kontaktinformasjon": {
                  "id": 100000123,
                  "enhetNr": "1234",
                  "telefonnummer": "12345678",
                  "telefonnummerKommentar": null,
                  "faksnummer": "12345678",
                  "epost": null,
                  "postadresse": {
                    "type": "postboksadresse",
                    "postnummer": "1234",
                    "poststed": "STED",
                    "postboksnummer": "6547",
                    "postboksanlegg": "Gate"
                  },
                  "besoeksadresse": {
                    "type": "stedsadresse",
                    "postnummer": "1234",
                    "poststed": "STED",
                    "gatenavn": "Gate",
                    "husnummer": "1",
                    "husbokstav": null,
                    "adresseTilleggsnavn": null
                  },
                  "spesielleOpplysninger": null,
                  "publikumsmottak": []
                }
              }
        """.trimIndent()

        val enhetKontaktinformasjonDTO = jackson.readValue(json, object : TypeReference<RsEnhetInkludertKontaktinformasjonDTO>() {})
        val enhetKontaktinformasjon = NorgApiImpl.toInternalDomain(enhetKontaktinformasjonDTO)
        assertTrue(enhetKontaktinformasjonDTO is RsEnhetInkludertKontaktinformasjonDTO)
        assertTrue(enhetKontaktinformasjon is NorgDomain.EnhetKontaktinformasjon)
    }
}
