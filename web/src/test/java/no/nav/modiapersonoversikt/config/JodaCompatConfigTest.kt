package no.nav.modiapersonoversikt.config

import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource

@JsonTest
@Import(JodaCompatConfig::class)
@TestPropertySource(properties = ["spring.main.allow-bean-definition-overriding=true"])
class JodaCompatConfigTest {
    @Autowired
    lateinit var jacksonTester: JacksonTester<DateTimeWrapper>

    @Test
    fun `DateTime serialiserer til ISO-string`() {
        val dt = DateTime.parse("2024-01-15T10:30:00.000Z")
        val json = jacksonTester.write(DateTimeWrapper(dt))
        assertTrue(json.json.contains("2024-01-15"), "Forventa ISO-dato i JSON: ${json.json}")
    }

    @Test
    fun `DateTime deserialiserer frå ISO-string`() {
        val result = jacksonTester.parse("""{"value":"2024-01-15T10:30:00.000Z"}""")
        assertTrue(result.`object`.value.year == 2024)
    }

    data class DateTimeWrapper(
        val value: DateTime,
    )
}
