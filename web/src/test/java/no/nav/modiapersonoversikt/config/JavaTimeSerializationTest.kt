package no.nav.modiapersonoversikt.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester
import java.time.OffsetDateTime

@JsonTest
class JavaTimeSerializationTest {
    @Autowired
    lateinit var jacksonTester: JacksonTester<DateTimeWrapper>

    @Test
    fun `OffsetDateTime serialiserer til ISO-string`() {
        val dateTime = OffsetDateTime.parse("2024-01-15T10:30:00.000Z")
        val json = jacksonTester.write(DateTimeWrapper(dateTime))

        assertTrue(json.json.contains("2024-01-15T10:30:00"))
        assertTrue(json.json.contains("Z"))
    }

    @Test
    fun `OffsetDateTime deserialiserer fra ISO-string`() {
        val expected = OffsetDateTime.parse("2024-01-15T10:30:00Z")
        val result = jacksonTester.parse("""{"value":"2024-01-15T10:30:00Z"}""")

        assertEquals(expected, result.`object`.value)
    }

    data class DateTimeWrapper(
        val value: OffsetDateTime,
    )
}
