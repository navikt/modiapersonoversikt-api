package no.nav.modiapersonoversikt.consumer.krr

import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import java.time.LocalDate

object Krr {
    interface Service : Pingable {
        fun hentDigitalKontaktinformasjon(fnr: String): DigitalKontaktinformasjon
    }

    data class DigitalKontaktinformasjon(
        val personident: String? = null,
        val reservasjon: Reservasjon? = null,
        val epostadresse: Epostadresse? = null,
        val mobiltelefonnummer: MobilTelefon? = null,
    )

    data class Epostadresse(
        val value: String? = null,
        val sistOppdatert: LocalDate? = null,
        val sistVerifisert: LocalDate? = null,
    )

    data class MobilTelefon(
        val value: String? = null,
        val sistOppdatert: LocalDate? = null,
        val sistVerifisert: LocalDate? = null,
    )

    data class Reservasjon(
        val value: Boolean? = null,
        val sistOppdatert: LocalDate? = null,
    )

    val INGEN_KONTAKTINFO =
        DigitalKontaktinformasjon(
            reservasjon = null,
            epostadresse = Epostadresse(value = ""),
            mobiltelefonnummer = MobilTelefon(value = ""),
        )
}
