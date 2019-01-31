package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.utbetaling

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import no.nav.modig.core.exception.ApplicationException
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

private const val FNR = "10108000398"
private const val DATO_START = "2018-01-01"
private const val DATO_SLUTT = "2018-10-01"
private const val FEIL_DATO_FORMAT = "234-14-7"

internal class UtbetalingControllerTest {

    private val service: UtbetalingService = mock()
    private val unleashService: UnleashService = mock()

    private val controller: UtbetalingController = UtbetalingController(service, unleashService)

    @Test
    fun `Kaster ApplicationException`() {
        whenever(service.hentWSUtbetalinger(any(), any(), any())).thenThrow(ApplicationException(""))
        assertFailsWith<ApplicationException> { controller.hent(FNR, DATO_START, DATO_SLUTT) }
    }

    @Test
    fun `Kaster feil ved feil dato`() {
        assertFailsWith<ApplicationException> { controller.hent(FNR, FEIL_DATO_FORMAT, FEIL_DATO_FORMAT) }
    }

}