package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

data class Krav(
    // TODO: Potensielt erstatt String med en mer passende type når vi vet hvordan "kravtype" er definert
    val kravType: String,
    val opprinneligBeløp: Double,
    val gjenståendeBeløp: Double?,
)
