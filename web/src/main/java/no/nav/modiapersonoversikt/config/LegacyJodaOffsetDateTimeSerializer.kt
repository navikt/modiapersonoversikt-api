package no.nav.modiapersonoversikt.config

import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ser.std.StdSerializer
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class LegacyJodaOffsetDateTimeSerializer : StdSerializer<OffsetDateTime>(OffsetDateTime::class.java) {
    override fun serialize(
        value: OffsetDateTime,
        gen: JsonGenerator,
        ctxt: SerializationContext,
    ) {
        gen.writeString(value.format(FORMATTER))
    }

    private companion object {
        val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    }
}
