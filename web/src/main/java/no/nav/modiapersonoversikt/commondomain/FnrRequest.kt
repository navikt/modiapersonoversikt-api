package no.nav.modiapersonoversikt.commondomain

data class FnrRequest(val fnr: String)

data class FnrDatoRangeRequest(val fnr: String, val fom: String, val tom: String)
