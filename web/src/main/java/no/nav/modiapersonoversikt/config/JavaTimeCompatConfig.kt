package no.nav.modiapersonoversikt.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.module.SimpleModule
import tools.jackson.databind.ser.std.StdSerializer
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Configuration
open class JavaTimeCompatConfig {
    @Bean
    open fun javaTimeCompatModule(): SimpleModule =
        SimpleModule("JavaTimeCompat").also { module ->
            module.addSerializer(OffsetDateTime::class.java, OffsetDateTimeSerializer())
        }

    private class OffsetDateTimeSerializer : StdSerializer<OffsetDateTime>(OffsetDateTime::class.java) {
        override fun serialize(
            value: OffsetDateTime,
            gen: JsonGenerator,
            ctxt: SerializationContext,
        ) {
            gen.writeString(value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")))
        }
    }
}
