package no.nav.modiapersonoversikt.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import kotlinx.datetime.*

object LocalDateTimeAsStringSerializer : JsonSerializer<LocalDateTime>() {
    override fun serialize(value: LocalDateTime?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeString(value.toString())
    }
}
