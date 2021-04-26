package no.nav.sbl.dialogarena.naudit

import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals

internal class ArchSightCEFLoggerTest {
    val cefHeader: String = "CEF:0|modia|personoversikt|1.0|audit:access|SporingsLogger|INFO|end=%s"

    @Test
    fun `standard CEF-format`() {
        val time = Instant.now().toEpochMilli()
        val expected = String.format("$cefHeader act=UPDATE suid=Z999999 sproc=saksbehandler.valgtenhet", time.toString())
        val message = cefLogger.create(
            CEFEvent(
                action = Audit.Action.UPDATE,
                resource = AuditResources.Saksbehandler.ValgtEnhet,
                subject = "Z999999",
                time = time,
                identifiers = arrayOf()
            )
        )

        assertEquals(expected, message)
    }

    @Test
    fun `CEF-format med ekstra ider`() {
        val time = Instant.now().toEpochMilli()
        val expected = String.format("$cefHeader act=UPDATE suid=Z999999 sproc=saksbehandler.valgtenhet duid=12345678910 flexString1=DOK1231 flexString1Label=DOKUMENT_REFERERANSE flexString2=JO9876 flexString2Label=JOURNALPOST_ID cs3=BI4567 cs3Label=BEHANDLING_ID", time.toString())
        val message = cefLogger.create(
            CEFEvent(
                action = Audit.Action.UPDATE,
                resource = AuditResources.Saksbehandler.ValgtEnhet,
                subject = "Z999999",
                time = time,
                identifiers = arrayOf(
                    AuditIdentifier.FNR to "12345678910",
                    AuditIdentifier.DOKUMENT_REFERERANSE to "DOK1231",
                    AuditIdentifier.JOURNALPOST_ID to "JO9876",
                    AuditIdentifier.BEHANDLING_ID to "BI4567"
                )
            )
        )

        assertEquals(expected, message)
    }
}
