package no.nav.modiapersonoversikt.rest.utbetaling

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.rest.DATOFORMAT
import no.nav.modiapersonoversikt.rest.lagRiktigDato
import no.nav.modiapersonoversikt.service.utbetaling.UtbetalingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/rest/utbetaling/{fnr}")
class UtbetalingController @Autowired constructor(
    private val service: UtbetalingService,
    private val tilgangskontroll: Tilgangskontroll
) {
    class UtbetalingerResponseDTO(
        val utbetalinger: List<UtbetalingDTO>,
        val periode: UtbetalingerPeriodeDTO
    )
    class UtbetalingerPeriodeDTO(
        val startDato: String,
        val sluttDato: String
    )
    @GetMapping
    fun hent(
        @PathVariable("fnr") fnr: String,
        @RequestParam("startDato") start: String?,
        @RequestParam("sluttDato") slutt: String?
    ): UtbetalingerResponseDTO {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Utbetalinger, AuditIdentifier.FNR to fnr)) {
                val startDato = lagRiktigDato(start)
                val sluttDato = lagRiktigDato(slutt)

                if (startDato == null || sluttDato == null) {
                    throw ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "queryparam ?startDato=yyyy-MM-dd&sluttDato=yyyy-MM-dd må være satt"
                    )
                } else {
                    val utbetalinger = UtbetalingMapper.hentUtbetalinger(
                        service.hentWSUtbetalinger(
                            fnr,
                            startDato,
                            sluttDato
                        )
                    )
                    UtbetalingerResponseDTO(
                        utbetalinger = utbetalinger,
                        periode = UtbetalingerPeriodeDTO(
                            startDato = startDato.toString(DATOFORMAT),
                            sluttDato = sluttDato.toString(DATOFORMAT),
                        )
                    )
                }
            }
    }
}
