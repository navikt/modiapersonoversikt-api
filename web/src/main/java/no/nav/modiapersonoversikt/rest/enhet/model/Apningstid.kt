package no.nav.modiapersonoversikt.rest.enhet.model

data class Apningstid(
    val ukedag: String,
    val apentFra: Klokkeslett,
    val apentTil: Klokkeslett
)
