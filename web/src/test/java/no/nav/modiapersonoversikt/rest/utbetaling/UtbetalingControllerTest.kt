package no.nav.modiapersonoversikt.rest.utbetaling

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.infrastructure.core.exception.ApplicationException
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.legacy.utbetaling.service.UtbetalingService
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private const val FNR = "10108000398"
private const val DATO_START = "2018-01-01"
private const val DATO_SLUTT = "2018-10-01"
private const val FEIL_DATO_FORMAT = "234-14-7"

internal class UtbetalingControllerTest {

    private val service: UtbetalingService = mockk()

    private val controller: UtbetalingController = UtbetalingController(service, TilgangskontrollMock.get())

    @Test
    fun `Kaster ApplicationException`() {
        every { service.hentWSUtbetalinger(any(), any(), any()) } throws ApplicationException("")
        assertFailsWith<ApplicationException> { controller.hent(FNR, DATO_START, DATO_SLUTT) }
    }

    @Test
    fun `Kaster feil ved feil dato`() {
        assertFailsWith<ApplicationException> { controller.hent(FNR, FEIL_DATO_FORMAT, FEIL_DATO_FORMAT) }
    }

    @Test
    fun `Kaster feil ved mangel på dato`() {
        assertEquals(400, controller.hent(FNR, null, null).statusCode.value())
    }
}
