package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.utbetaling

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.DATOFORMAT
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.lagRiktigDato
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*
import org.joda.time.LocalDate
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON

private const val DAGER_BAKOVER = 30
private const val DAGER_FREMOVER = 100

@Path("/utbetaling/{fnr}")
@Produces(APPLICATION_JSON)
class UtbetalingController @Inject constructor(private val service: UtbetalingService,
                                               private val unleashService: UnleashService) {

    @GET
    @Path("/")
    fun hent(@PathParam("fnr") fødselsnummer: String,
             @QueryParam("startDato") start: String?,
             @QueryParam("sluttDato") slutt: String?): Map<String, Any?> {
        check(unleashService.isEnabled(Feature.NYTT_VISITTKORT))

        val startDato = lagRiktigDato(start) ?: LocalDate.now().minusDays(DAGER_BAKOVER)
        val sluttDato = (lagRiktigDato(slutt) ?: LocalDate.now()).plusDays(DAGER_FREMOVER)
        val utbetalinger = service.hentWSUtbetalinger(fødselsnummer,
                startDato,
                sluttDato)

        return mapOf(
                "utbetalinger" to hentUtbetalinger(utbetalinger),
                "periode" to mapOf(
                        "startDato" to startDato.toString(DATOFORMAT),
                        "sluttDato" to sluttDato.toString(DATOFORMAT)
                )
        )
    }

    private fun hentUtbetalinger(utbetalinger: List<WSUtbetaling>): List<Map<String, Any?>> {
        return utbetalinger.map {
            mapOf(
                    "posteringsdato" to it.posteringsdato?.toString(DATOFORMAT),
                    "utbetalingsdato" to it.utbetalingsdato?.toString(DATOFORMAT),
                    "forfallsdato" to it.forfallsdato?.toString(DATOFORMAT),
                    "utbetaltTil" to it.utbetaltTil?.navn?.trim(),
                    "erUtbetaltTilPerson" to (it.utbetaltTil is WSPerson),
                    "erUtbetaltTilOrganisasjon" to (it.utbetaltTil is WSOrganisasjon),
                    "erUtbetaltTilSamhandler" to (it.utbetaltTil is WSSamhandler),
                    "nettobeløp" to it.utbetalingNettobeloep,
                    "melding" to it.utbetalingsmelding?.trim(),
                    "metode" to it.utbetalingsmetode?.trim(),
                    "status" to it.utbetalingsstatus?.trim(),
                    "konto" to it.utbetaltTilKonto?.kontonummer?.trim(),
                    "ytelser" to it.ytelseListe?.let { hentYtelserForUtbetaling(it) }
            )
        }
    }

    private fun hentYtelserForUtbetaling(ytelser: List<WSYtelse>): List<Map<String, Any?>> {
        return ytelser.map {
            mapOf(
                    "type" to it.ytelsestype?.value?.trim(),
                    "ytelseskomponentListe" to it.ytelseskomponentListe?.let { hentYtelsekomponentListe(it) },
                    "ytelseskomponentersum" to it.ytelseskomponentersum,
                    "trekkListe" to it.trekkListe?.let { hentTrekkListe(it) },
                    "trekksum" to it.trekksum,
                    "skattListe" to it.skattListe?.let { hentSkattListe(it) },
                    "skattsum" to it.skattsum,
                    "periode" to it.ytelsesperiode?.let { hentYtelsesperiode(it) },
                    "nettobeløp" to it.ytelseNettobeloep,
                    "bilagsnummer" to it.bilagsnummer?.trim()
            )
        }
    }

    private fun hentYtelsekomponentListe(ytelseskomponenter: List<WSYtelseskomponent>): List<Map<String, Any?>> {
        return ytelseskomponenter.map {
            mapOf(
                    "ytelseskomponenttype" to it.ytelseskomponenttype?.trim(),
                    "satsbeløp" to it.satsbeloep,
                    "satstype" to it.satstype?.trim(),
                    "satsantall" to it.satsantall,
                    "ytelseskomponentbeløp" to it.ytelseskomponentbeloep
            )
        }
    }

    private fun hentTrekkListe(trekk: List<WSTrekk>): List<Map<String, Any?>> {
        return trekk.map {
            mapOf(
                    "trekktype" to it.trekktype?.trim(),
                    "trekkbeløp" to it.trekkbeloep,
                    "kreditor" to it.kreditor?.trim()
            )
        }
    }

    private fun hentSkattListe(skatt: List<WSSkatt>): List<Map<String, Any?>> {
        return skatt.map {
            mapOf(
                    "skattebeløp" to it.skattebeloep
            )
        }
    }

    private fun hentYtelsesperiode(periode: WSPeriode): Map<String, Any?> {
        return mapOf(
                "start" to periode.fom?.toString(DATOFORMAT),
                "slutt" to periode.tom?.toString(DATOFORMAT)
        )
    }

}
