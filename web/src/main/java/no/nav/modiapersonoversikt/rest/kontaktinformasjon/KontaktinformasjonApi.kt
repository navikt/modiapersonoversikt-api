package no.nav.modiapersonoversikt.rest.kontaktinformasjon

import java.time.LocalDate

object KontaktinformasjonApi {
    data class Kontaktinformasjon(
        val epost: Verdi<String>?,
        val mobiltelefon: Verdi<String>?,
        val reservasjon: Verdi<Boolean>?,
    )

    data class Verdi<T>(
        val value: T,
        val sistOppdatert: LocalDate?,
    )
}
