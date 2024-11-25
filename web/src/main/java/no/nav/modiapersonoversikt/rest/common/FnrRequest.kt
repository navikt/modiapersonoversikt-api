package no.nav.modiapersonoversikt.rest.common

import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.IdentType

data class FnrRequest(
    val fnr: String,
)

data class KravRequest(
    val ident: String,
    val identType: IdentType,
)

data class FnrDatoRangeRequest(
    val fnr: String,
    val fom: String? = null,
    val tom: String? = null,
)
