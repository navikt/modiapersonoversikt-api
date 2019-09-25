package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.utbetaling

import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.DATOFORMAT
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.lagRiktigDato
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response

@Path("/utbetaling/{fnr}")
@Produces(APPLICATION_JSON)
class UtbetalingController @Inject constructor(private val service: UtbetalingService) {

    @GET
    @Path("/")
    fun hent(@PathParam("fnr") fødselsnummer: String,
             @QueryParam("startDato") start: String?,
             @QueryParam("sluttDato") slutt: String?): Response {
        // TODO tilgangsstyring
        val startDato = lagRiktigDato(start)
        val sluttDato = lagRiktigDato(slutt)

        if (startDato == null || sluttDato == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("queryparam ?startDato=yyyy-MM-dd&sluttDato=yyyy-MM-dd må være satt").build()
        }

        val utbetalinger = service.hentWSUtbetalinger(fødselsnummer,
                startDato,
                sluttDato)

        return Response.ok(mapOf(
                "utbetalinger" to hentUtbetalinger(utbetalinger),
                "periode" to mapOf(
                        "startDato" to startDato.toString(DATOFORMAT),
                        "sluttDato" to sluttDato.toString(DATOFORMAT)
                )
        )).build()
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
                    "bilagsnummer" to it.bilagsnummer?.trim(),
                    "arbeidsgiver" to it.refundertForOrg?.let { hentArbeidsgiver(it) }
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

    private fun hentArbeidsgiver(it: WSAktoer): Map<String, String> {
        return mapOf(
                "orgnr" to it.aktoerId,
                "navn" to it.navn
        )
    }
}
