package no.nav.modiapersonoversikt.rest.utbetaling

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.utbetaling.UtbetalingDomain
import no.nav.modiapersonoversikt.service.utbetaling.UtbetalingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/rest/utbetaling/{fnr}")
class UtbetalingController @Autowired constructor(
    private val service: UtbetalingService,
    private val tilgangskontroll: Tilgangskontroll,
) {
    class UtbetalingerResponseDTO(
        val utbetalinger: List<UtbetalingDomain.Utbetaling>,
        val periode: UtbetalingerPeriodeDTO
    )
    class UtbetalingerPeriodeDTO(
        val startDato: LocalDate,
        val sluttDato: LocalDate
    )

    @GetMapping
    fun hent(
        @PathVariable("fnr") fnr: String,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("startDato")
        start: LocalDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("sluttDato")
        slutt: LocalDate
    ): UtbetalingerResponseDTO {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Utbetalinger, AuditIdentifier.FNR to fnr)) {
                val utbetalinger = service.hentUtbetalinger(Fnr(fnr), start, slutt)
                UtbetalingerResponseDTO(
                    utbetalinger = utbetalinger,
                    periode = UtbetalingerPeriodeDTO(
                        startDato = start,
                        sluttDato = slutt,
                    )
                )
            }
    }
}
