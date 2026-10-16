package no.nav.modiapersonoversikt.config

import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.sakstema.domain.Sak
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester
import java.time.OffsetDateTime
import java.util.Optional

@JsonTest
class DateTimeWireFormatCompatibilityTest {
    @Autowired
    lateinit var sakTester: JacksonTester<JournalforingSak>

    @Autowired
    lateinit var offsetDateTimeTester: JacksonTester<OffsetDateTimeWrapper>

    @Autowired
    lateinit var sakstemaTester: JacksonTester<Sak>

    @Test
    fun `serializes migrated Joda timestamps using the existing ISO wire format`() {
        val sak =
            JournalforingSak().apply {
                opprettetDato = OffsetDateTime.parse("2024-01-15T10:30:00Z")
            }

        val json = sakTester.write(sak).json

        assertTrue(json.contains(""""opprettetDato":"2024-01-15T10:30:00.000Z""""))
    }

    @Test
    fun `serializes migrated Joda case timestamps using the existing ISO wire format`() {
        val sak =
            Sak().withOpprettetDato(
                Optional.of(OffsetDateTime.parse("2024-01-15T10:30:00Z")),
            )

        val json = sakstemaTester.write(sak).json

        assertTrue(json.contains(""""opprettetDato":"2024-01-15T10:30:00.000Z""""))
    }

    @Test
    fun `preserves sub-millisecond precision for existing Java-time values`() {
        val timestamp = OffsetDateTime.parse("2024-01-15T10:30:00.123456789Z")

        val json = offsetDateTimeTester.write(OffsetDateTimeWrapper(timestamp))

        assertEquals("""{"value":"2024-01-15T10:30:00.123456789Z"}""", json.json)
    }

    data class OffsetDateTimeWrapper(
        val value: OffsetDateTime,
    )
}
