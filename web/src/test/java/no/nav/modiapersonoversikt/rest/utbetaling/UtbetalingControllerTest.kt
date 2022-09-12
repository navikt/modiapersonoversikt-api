package no.nav.modiapersonoversikt.rest.utbetaling

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.service.utbetaling.UtbetalingService
import no.nav.modiapersonoversikt.service.utbetaling.WSUtbetalingService
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private const val FNR = "10108000398"
private const val DATO_START = "2018-01-01"
private const val DATO_SLUTT = "2018-10-01"
private const val FEIL_DATO_FORMAT = "234-14-7"

internal class UtbetalingControllerTest {

    private val service: WSUtbetalingService = mockk()
    private val restService: UtbetalingService = mockk()
    private val unleash: UnleashService = mockk()

    private val controller: UtbetalingController = UtbetalingController(service, restService, unleash, TilgangskontrollMock.get())

    @Test
    fun `Kaster ApplicationException`() {
        every { service.hentWSUtbetalinger(any(), any(), any()) } throws RuntimeException("")
        every { restService.hentUtbetalinger(any(), any(), any()) } returns emptyList()
        assertFailsWith<RuntimeException> { controller.hent(FNR, DATO_START, DATO_SLUTT) }
    }

    @Test
    fun `Kaster feil ved feil dato`() {
        assertFailsWith<RuntimeException> { controller.hent(FNR, FEIL_DATO_FORMAT, FEIL_DATO_FORMAT) }
    }

    @Test
    fun `Kaster feil ved mangel på dato`() {
        val exception = assertFailsWith<ResponseStatusException> {
            controller.hent(FNR, null, null)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)
    }
}
