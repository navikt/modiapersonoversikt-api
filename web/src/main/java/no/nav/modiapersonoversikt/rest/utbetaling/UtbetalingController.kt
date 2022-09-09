package no.nav.modiapersonoversikt.rest.utbetaling

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.rest.DATOFORMAT
import no.nav.modiapersonoversikt.rest.lagRiktigDato
import no.nav.modiapersonoversikt.service.utbetaling.UtbetalingDomain
import no.nav.modiapersonoversikt.service.utbetaling.UtbetalingService
import no.nav.modiapersonoversikt.service.utbetaling.WSUtbetalingMapper
import no.nav.modiapersonoversikt.service.utbetaling.WSUtbetalingService
import no.nav.modiapersonoversikt.utils.ConvertionUtils.toJavaTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/rest/utbetaling/{fnr}")
class UtbetalingController @Autowired constructor(
    private val service: WSUtbetalingService,
    private val restService: UtbetalingService,
    private val tilgangskontroll: Tilgangskontroll,
) {
    val mappingExperiment = Scientist.createExperiment<List<UtbetalingDomain.Utbetaling>>(
        Scientist.Config(
            name = "UtbetalingRest",
            experimentRate = Scientist.FixedValueRate(0.1)
        )
    )

    class UtbetalingerResponseDTO(
        val utbetalinger: List<UtbetalingDomain.Utbetaling>,
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
                    val utbetalinger = mappingExperiment.run(
                        control = {
                            WSUtbetalingMapper.hentUtbetalinger(
                                service.hentWSUtbetalinger(
                                    fnr,
                                    startDato,
                                    sluttDato
                                )
                            )
                        },
                        experiment = {
                            restService.hentUtbetalinger(Fnr(fnr), startDato.toJavaTime(), sluttDato.toJavaTime())
                        },
                        dataFields = ::utbetalingSammenligning
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

    fun utbetalingSammenligning(
        wsUtbetalinger: List<UtbetalingDomain.Utbetaling>,
        anyTry: Scientist.UtilityClasses.Try<Any?>
    ): Map<String, Any?> {
        if (anyTry.isFailure) return emptyMap()
        val restUtbetalinger = anyTry.getOrThrow() as List<UtbetalingDomain.Utbetaling>

        val transformerteWsUtbetalinger = wsUtbetalinger.map { utbetaling ->
            utbetaling.copy(
                ytelser = utbetaling.ytelser.map { ytelse ->
                    ytelse.copy(
                        ytelseskomponentListe = ytelse.ytelseskomponentListe.map { ytelsekomponent ->
                            ytelsekomponent.copy(
                                satsantall = ytelsekomponent.satsantall ?: 0.0 // Rest fyller inn 0.0 istedet for null
                            )
                        },

                        // SOAP endepunktet kan returnere -0.0, dette er fikset i nye rest-endepunkter
                        trekksum = if (ytelse.trekksum == -0.0) 0.0 else ytelse.trekksum,
                        skattsum = if (ytelse.skattsum == -0.0) 0.0 else ytelse.skattsum,

                        // SOAP returnerer dummy-objekt ved manglende arbeidsgiver. Dette feltet blir null i rest-apiet
                        arbeidsgiver = if (ytelse.arbeidsgiver?.orgnr == "000000000") null else ytelse.arbeidsgiver
                    )
                }
            )
        }

        val (ok, controlJson, experimentJson) = Scientist.compareAndSerialize(transformerteWsUtbetalinger, restUtbetalinger)
        // Overskriver standard scientist felter etter å ha gjort en sammenligning med de kjente endringene i apiet
        return mapOf(
            "ok" to ok,
            "control" to controlJson,
            "experiment" to experimentJson,
        )
    }
}
