package no.nav.modiapersonoversikt.rest.common

data class FnrRequest(
    val fnr: String,
)

data class FnrDatoRangeRequest(
    val fnr: String,
    val fom: String? = null,
    val tom: String? = null,
)
