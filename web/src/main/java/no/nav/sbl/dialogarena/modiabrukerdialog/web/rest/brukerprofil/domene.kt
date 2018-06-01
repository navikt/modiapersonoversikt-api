package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.brukerprofil

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
            val områdeadresse: Områdeadresse? = null,
            val postboksadresse: Postboksadresse? = null
    ) {
        data class Gateadresse(
                val co: String,
                val gatenavn: String,
                val husnummer: String,
                val husbokstav: String,
                val bolignummer: String,
                val postnummer: String,
                val gyldigTil: LocalDate
        )
        data class Områdeadresse(
                val co: String,
                val områdeadresse: String,
                val postnummer: String,
                val gyldigTil: LocalDate
        )
        data class Postboksadresse(
                val co: String,
                val postboksnummer: String,
                val postboksanleggnavn: String,
                val postnummer: String,
                val gyldigTil: LocalDate
        )
    }

    data class UtenlandskAdresse (
        val landkode: String,
        val adresselinje1: String,
        val adresselinje2: String,
        val adresselinje3: String,
        val gyldigTil: LocalDate
    )

}