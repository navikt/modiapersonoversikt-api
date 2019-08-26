package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.brukerprofil

import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.informasjon.FimTilrettelagtKommunikasjon
import java.time.LocalDate

data class EndreNavnRequest(val fødselsnummer: String = "",
                            val fornavn: String = "",
                            val mellomnavn: String = "",
                            val etternavn: String = "")

data class EndreAdresseRequest(
        val norskAdresse: NorskAdresse? = null,
        val utenlandskAdresse: UtenlandskAdresse? = null,
        val folkeregistrertAdresse: Boolean = false
) {
    data class NorskAdresse(
            val gateadresse: Gateadresse? = null,
            val matrikkeladresse: Matrikkeladresse? = null,
            val postboksadresse: Postboksadresse? = null
    ) {
        data class Gateadresse(
                val tilleggsadresse: String? = null,
                val gatenavn: String,
                val husnummer: String? = null,
                val husbokstav: String? = null,
                val bolignummer: String? = null,
                val postnummer: String,
                val gyldigTil: LocalDate
        )
        data class Matrikkeladresse(
                val tilleggsadresse: String? = null,
                val eiendomsnavn: String,
                val postnummer: String,
                val gyldigTil: LocalDate
        )
        data class Postboksadresse(
                val tilleggsadresse: String? = null,
                val postboksnummer: String,
                val postboksanlegg: String? = null,
                val postnummer: String,
                val gyldigTil: LocalDate
        )
    }

    data class UtenlandskAdresse (
        val landkode: String,
        val adresselinje1: String,
        val adresselinje2: String? = null,
        val adresselinje3: String? = null,
        val gyldigTil: LocalDate
    )

}

data class EndreTelefonnummerRequest(
        val hjem: Telefon? = null,
        val jobb: Telefon? = null,
        val mobil: Telefon? = null
) {
    data class Telefon(val identifikator: String, val retningsnummer: String)
}

data class EndreKontonummerRequest(
        val kontonummer: String,
        val landkode: String? = null,
        val valuta: String? = null,
        val banknavn: String? = null,
        val bankkode: String? = null,
        val swift: String? = null,
        val bankadresse: Adresse? = null
) {
    data class Adresse(val linje1: String, var linje2: String, var linje3: String)
}

data class EndreTilrettelagtkommunikasjonRequest (
        val tilrettelagtKommunikasjon: List<String>
)
