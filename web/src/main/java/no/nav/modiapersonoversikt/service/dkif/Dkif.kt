package no.nav.modiapersonoversikt.service.dkif

import java.time.LocalDate

object Dkif {
    interface Service {
        fun hentDigitalKontaktinformasjon(fnr: String): DigitalKontaktinformasjon
    }

    data class DigitalKontaktinformasjon(
        val personident: String? = null,
        val reservasjon: String? = null,
        val epostadresse: Epostadresse? = null,
        val mobiltelefonnummer: MobilTelefon? = null
    )

    data class Epostadresse(
        val value: String? = null,
        val sistOppdatert: LocalDate? = null,
        val sisVerifisert: LocalDate? = null
    )

    data class MobilTelefon(
        val value: String? = null,
        val sistOppdatert: LocalDate? = null,
        val sisVerifisert: LocalDate? = null
    )

    val INGEN_KONTAKTINFO = DigitalKontaktinformasjon(
        reservasjon = "",
        epostadresse = Dkif.Epostadresse(value = ""),
        mobiltelefonnummer = Dkif.MobilTelefon(value = "")
    )
}
