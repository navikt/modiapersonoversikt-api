package no.nav.modiapersonoversikt.config

import org.joda.time.DateTime
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.core.JacksonException
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.deser.std.StdDeserializer
import tools.jackson.databind.module.SimpleModule
import tools.jackson.databind.ser.std.StdSerializer

/**
 * Midlertidig støtte for Joda-Time i Jackson 3 (Spring Boot 4).
 * Fjernast når domeneklassane er migrerte til java.time — sjå backlog-oppgåva.
 */
@Configuration
open class JodaCompatConfig {
    @Bean
    open fun jodaCompatModule(): SimpleModule =
        SimpleModule("JodaCompat").also { module ->
            module.addSerializer(DateTime::class.java, DateTimeSerializer())
            module.addDeserializer(DateTime::class.java, DateTimeDeserializer())
        }

    private class DateTimeSerializer : StdSerializer<DateTime>(DateTime::class.java) {
        override fun serialize(
            value: DateTime,
            gen: JsonGenerator,
            ctxt: SerializationContext,
        ) {
            gen.writeString(value.toString())
        }
    }

    private class DateTimeDeserializer : StdDeserializer<DateTime>(DateTime::class.java) {
        @Throws(JacksonException::class)
        override fun deserialize(
            p: JsonParser,
            ctxt: DeserializationContext,
        ): DateTime = DateTime.parse(p.valueAsString)
    }
}
