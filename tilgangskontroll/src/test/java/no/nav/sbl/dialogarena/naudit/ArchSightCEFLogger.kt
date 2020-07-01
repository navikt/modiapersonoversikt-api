package no.nav.sbl.dialogarena.naudit

import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals

internal class ArchSightCEFLoggerTest {
    @Test
    fun `CEF-format`() {
        val time = Instant.now().toEpochMilli()
        val expected = String.format("CEF:0|modia|personoversikt|1.0|audit:access|SporingsLogger|INFO|end=%s act=UPDATE suid=Z999999 sproc=saksbehandler.valgtenhet", time.toString())
        val message = cefLogger.create(CEFEvent(
                action = Audit.Action.UPDATE,
                resource = AuditResources.Saksbehandler.ValgtEnhet,
                subject = "Z999999",
                time = time,
                identifiers = arrayOf()
        ))

        assertEquals(expected, message)
    }
}