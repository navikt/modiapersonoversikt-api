package no.nav.modiapersonoversikt.service.utbetaling

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import java.time.LocalDate

interface UtbetalingService : Pingable {
    fun hentUtbetalinger(
        fnr: Fnr,
        startDato: LocalDate,
        sluttDato: LocalDate,
    ): List<UtbetalingDomain.Utbetaling>
}
