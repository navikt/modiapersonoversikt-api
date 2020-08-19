package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse

import no.nav.common.auth.subject.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.DelsvarRequest.DelsvarRequestBuilder
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.DelsvarService
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.*
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import no.nav.sbl.dialogarena.naudit.AuditResources.Person.Henvendelse

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
                .get(Audit.describe(CREATE, Henvendelse.Delsvar, AuditIdentifier.FNR to fnr, AuditIdentifier.BEHANDLING_ID to request.behandlingsId)) {
                    val saksbehandlersValgteEnhet = RestUtils.hentValgtEnhet(httpRequest)

                    val delsvarRequest = DelsvarRequestBuilder()
                            .withFodselsnummer(fnr)
                            .withTraadId(request.traadId)
                            .withBehandlingsId(request.behandlingsId)
                            .withSvar(request.fritekst)
                            .withNavIdent(SubjectHandler.getIdent().get())
                            .withValgtEnhet(saksbehandlersValgteEnhet)
                            .withTemagruppe(request.temagruppe)
                            .withOppgaveId(request.oppgaveId)
                            .build()

                    try {
                        delsvarService.svarDelvis(delsvarRequest)
                    } catch (exception: RuntimeException) {
                        throw handterRuntimeFeil(exception)
                    }

                    Response.ok("{\"message\": \"Success\"}").build()
                }
    }

    private fun handterRuntimeFeil(exception: RuntimeException): RuntimeException {
        logger.error("Feil ved opprettelse av delvis svar", exception)
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
