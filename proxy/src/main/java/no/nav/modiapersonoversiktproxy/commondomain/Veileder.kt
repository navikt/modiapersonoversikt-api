package no.nav.modiapersonoversiktproxy.commondomain

data class Veileder(
    val fornavn: String,
    val etternavn: String,
    val ident: String,
) {
    val navn: String = "$fornavn $etternavn"
}
