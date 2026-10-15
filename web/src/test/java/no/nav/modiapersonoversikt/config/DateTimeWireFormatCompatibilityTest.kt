package no.nav.modiapersonoversikt.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.context.annotation.Import
import java.time.OffsetDateTime

@JsonTest
@Import(JavaTimeCompatConfig::class)
class DateTimeWireFormatCompatibilityTest {
    @Autowired
    lateinit var jacksonTester: JacksonTester<OffsetDateTimeWrapper>

    @Test
    fun `serializes timestamps using the existing ISO wire format`() {
        val timestamp = OffsetDateTime.parse("2024-01-15T10:30:00.000Z")

        val json = jacksonTester.write(OffsetDateTimeWrapper(timestamp))

        assertEquals("""{"value":"2024-01-15T10:30:00.000Z"}""", json.json)
    }

    @Test
    fun `deserializes timestamps in the existing ISO wire format`() {
        val json = jacksonTester.parse("""{"value":"2024-01-15T10:30:00.000Z"}""")

        assertEquals(OffsetDateTime.parse("2024-01-15T10:30:00Z"), json.`object`.value)
    }

    data class OffsetDateTimeWrapper(
        val value: OffsetDateTime,
    )
}
