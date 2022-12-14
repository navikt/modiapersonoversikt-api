package no.nav.modiapersonoversikt.rest.kontaktinformasjon

import java.time.LocalDate

object KontaktinformasjonApi {
    data class Kontaktinformasjon(
        val epost: Verdi?,
        val mobiltelefon: Verdi?,
        val reservasjon: String?,
    )

    data class Verdi(
        val value: String,
        val sistOppdatert: LocalDate?,
    )
}
