package no.nav.modiapersonoversikt.rest

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

/**
 * Brukes bare mens vi venter på at SF skal fikse dato-formatene sine
 */
class LenientDateModule : SimpleModule("lenient-date-module") {
    init {
        addDeserializer(OffsetDateTime::class.java, LenientDateDeserializer())
    }
}

class LenientDateDeserializer : JsonDeserializer<OffsetDateTime>() {
    private val fmt = DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        .optionalStart().appendOffset("+HH:MM", "+00:00").optionalEnd()
        .optionalStart().appendOffset("+HHMM", "+0000").optionalEnd()
        .optionalStart().appendOffset("+HH", "+00").optionalEnd()
        .toFormatter()

    override fun deserialize(parser: JsonParser, context: DeserializationContext): OffsetDateTime {
        return OffsetDateTime.parse(parser.text, fmt)
    }
}
