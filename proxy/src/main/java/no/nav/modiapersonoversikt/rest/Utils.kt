package no.nav.modiapersonoversikt.rest

import kotlinx.serialization.Serializable
import java.time.format.DateTimeFormatter

@Serializable
data class RequestBodyContent(
    val fnr: String,
    val start: String?,
    val slutt: String?,
)

val DATOFORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
