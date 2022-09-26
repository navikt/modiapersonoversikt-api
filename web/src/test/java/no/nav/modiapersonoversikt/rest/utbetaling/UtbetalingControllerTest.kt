package no.nav.modiapersonoversikt.rest.utbetaling

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.service.utbetaling.UtbetalingService
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertFailsWith

private const val FNR = "10108000398"

internal class UtbetalingControllerTest {
    private val DATO_START = LocalDate.parse("2018-01-01")
    private val DATO_SLUTT = LocalDate.parse("2018-10-01")

    private val service: UtbetalingService = mockk()
    private val controller: UtbetalingController = UtbetalingController(service, TilgangskontrollMock.get())

    @Test
    fun `Kaster ApplicationException`() {
        every { service.hentUtbetalinger(any(), any(), any()) } throws RuntimeException("")
        assertFailsWith<RuntimeException> { controller.hent(FNR, DATO_START, DATO_SLUTT) }
    }

    @Test
    fun `Kaster feil ved feil dato`() {
        assertFailsWith<RuntimeException> {
            val FEIL_DATO_FORMAT = LocalDate.parse("234-14-7")
            controller.hent(FNR, FEIL_DATO_FORMAT, FEIL_DATO_FORMAT)
        }
    }
}
