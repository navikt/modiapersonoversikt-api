package no.nav.modiapersonoversikt.consumer.norg

import com.fasterxml.jackson.core.type.TypeReference
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.infrastructure.Serializer
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.models.RsAdresseDTO
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.models.RsPostboksadresseDTO
import no.nav.modiapersonoversikt.legacy.api.domain.norg.generated.models.RsStedsadresseDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class NorgApiTest {
    val jackson = Serializer.jacksonObjectMapper

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
}
