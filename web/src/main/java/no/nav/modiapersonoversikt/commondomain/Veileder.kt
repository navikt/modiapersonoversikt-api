package no.nav.modiapersonoversikt.commondomain

/**
 * Collection of strings useful for displaying the sender of a message
 */
data class Veileder(
    val fornavn: String,
    val etternavn: String,
    val ident: String,
    val enhet: String? = null,
) {
    val navn: String = "$fornavn $etternavn"
}
