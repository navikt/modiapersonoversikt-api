package no.nav.modiapersonoversiktproxy.rest

import kotlinx.serialization.Serializable
import org.joda.time.format.DateTimeFormat
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

@Serializable
data class RequestBodyContent(
    val fnr: String,
    val start: String?,
    val slutt: String?,
)

val JODA_DATOFORMAT: org.joda.time.format.DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
val DATOFORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
val DATO_TID_FORMAT: DateTimeFormatter =
    DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral("T")
        .appendPattern("HH:mm:ss")
        .appendOffsetId()
        .toFormatter()
