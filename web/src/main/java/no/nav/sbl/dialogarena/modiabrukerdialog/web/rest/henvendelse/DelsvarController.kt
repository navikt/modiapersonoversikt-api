package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.DelsvarRequest.DelsvarRequestBuilder
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.DelsvarService
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

import javax.ws.rs.core.MediaType.APPLICATION_JSON
import no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler
import no.nav.metrics.MetricsFactory.createEvent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Tilgangskontroll

@Path("/dialog/{fnr}")
@Produces(APPLICATION_JSON)
class DelsvarController @Inject constructor(
        private val tilgangskontroll: Tilgangskontroll,
        private val delsvarService: DelsvarService
) {
    @POST
    @Path("/delvis-svar")
    @Consumes(APPLICATION_JSON)
    fun svarDelvis(
            @PathParam("fnr") fnr: String,
            @Context httpRequest: HttpServletRequest,
            request: DelsvarRestRequest): Response
    {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get {
                    val saksbehandlersValgteEnhet = RestUtils.hentValgtEnhet(httpRequest)

                    val delsvarRequest = DelsvarRequestBuilder()
                            .withFodselsnummer(fnr)
                            .withTraadId(request.traadId)
                            .withBehandlingsId(request.behandlingsId)
                            .withSvar(request.fritekst)
                            .withNavIdent(getSubjectHandler().uid)
                            .withValgtEnhet(saksbehandlersValgteEnhet)
                            .withTemagruppe(request.temagruppe)
                            .withOppgaveId(request.oppgaveId)
                            .build()

                    try {
                        delsvarService.svarDelvis(delsvarRequest)
                    } catch (exception: RuntimeException) {
                        throw handterRuntimeFeil(exception)
                    }

                    createEvent("hendelse.svardelviscontroller.svardelvis.fullfort").report()

                    Response.ok("{\"message\": \"Success\"}").build()
                }
    }

    private fun handterRuntimeFeil(exception: RuntimeException): RuntimeException {
        logger.error("Feil ved opprettelse av delvis svar", exception)
        createEvent("hendelse.svardelviscontroller.svardelvis.runtime-exception").report()
        return exception
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DelsvarController::class.java)
    }
}

data class DelsvarRestRequest(
        val fritekst: String,
        val traadId: String,
        val behandlingsId: String,
        val temagruppe: String,
        val oppgaveId: String
)
