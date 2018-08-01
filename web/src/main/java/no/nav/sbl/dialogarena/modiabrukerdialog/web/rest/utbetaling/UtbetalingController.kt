package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.utbetaling

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSUtbetaling
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON

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

        val startDato = start?.let { LocalDate(start) }
        val sluttDato = slutt?.let { LocalDate(slutt) }

        val utbetalinger = service.hentWSUtbetalinger(fødselsnummer,
                startDato ?: LocalDate.now().minusDays(30),
                sluttDato ?: LocalDate.now().plusDays(100))

        return mapOf(
                "utbetaling" to hentUtbetalinger(utbetalinger)
        )
    }

    private fun hentUtbetalinger(utbetalinger: List<WSUtbetaling>): List<Map<String, Any?>> {
        return utbetalinger.map {
            mapOf(
                "til" to it.utbetaltTil?.navn
            )
        }
    }

}