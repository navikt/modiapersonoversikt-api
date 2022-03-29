package no.nav.modiapersonoversikt.consumer.ldap

data class Saksbehandler(
    val fornavn: String,
    val etternavn: String,
    val ident: String
) {
    val navn: String = "$fornavn $etternavn"
}
