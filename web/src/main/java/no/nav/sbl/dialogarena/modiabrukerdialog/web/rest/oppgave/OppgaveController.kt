package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppgave

import no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler
import no.nav.metrics.MetricsFactory.createEvent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.LeggTilbakeOppgaveIGsakRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.CookieUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.mapOfNotNullOrEmpty
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.tilgangTilModia
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response

private val logger = LoggerFactory.getLogger(OppgaveController::class.java)
private const val HENT_OPPGAVE_ROLLE = "0000-GA-BD06_HentOppgave"

@Path("/oppgaver")
@Produces(APPLICATION_JSON)
class OppgaveController @Inject constructor(
        private val oppgaveBehandlingService: OppgaveBehandlingService,
        private val plukkOppgaveService: PlukkOppgaveService,
        private val ldapService: LDAPService,
        private val tilgangkontroll: Tilgangskontroll
) {

    @POST
    @Path("/{id}/leggTilbake")
    fun leggTilbake(@PathParam("id") oppgaveId: String, @Context httpRequest: HttpServletRequest, request: LeggTilbakeRequest): Response {
        return tilgangkontroll
                .tilgangTilModia()
                // TODO tilgangsstyring, burde også sjekke tilgang til oppgave
                .get {
                    val leggTilbakeOppgaveIGsakRequest = LeggTilbakeOppgaveIGsakRequest()
                            .withOppgaveId(oppgaveId)
                            .withBeskrivelse(request.beskrivelse)
                            .withTemagruppe(getTemagruppefraRequest(request.temagruppe))
                            .withSaksbehandlersValgteEnhet(CookieUtil.getSaksbehandlersValgteEnhet(httpRequest))

                    try {
                        oppgaveBehandlingService.leggTilbakeOppgaveIGsak(leggTilbakeOppgaveIGsakRequest)
                    } catch (exception: RuntimeException) {
                        throw handterRuntimeFeil(exception)
                    }

                    createEvent("hendelse.oppgavecontroller.leggtilbake.fullfort").report()
                    Response.ok("{\"message\": \"Success\"}").build()
                }
    }

    @POST
    @Path("/plukk/{temagruppe}")
    fun plukkOppgaver(@PathParam("temagruppe") temagruppe: String, @Context httpRequest: HttpServletRequest) =
            tilgangkontroll
                    .tilgangTilModia()
                    .get {
                        plukkOppgaveService
                                .also { verifiserTilgang(HENT_OPPGAVE_ROLLE) }
                                .plukkOppgaver(Temagruppe.valueOf(temagruppe.toUpperCase()),
                                        CookieUtil.getSaksbehandlersValgteEnhet(httpRequest))
                                .map { mapOppgave(it) }
                    }

    @GET
    @Path("/tildelt")
    fun finnTildelte() =
            tilgangkontroll
                    .tilgangTilModia()
                    .get {
                        oppgaveBehandlingService.finnTildelteOppgaverIGsak()
                                .map { mapOppgave(it) }
                    }

    private fun verifiserTilgang(rolle: String) {
        val consumerId = getSubjectHandler().uid
        if (!ldapService.saksbehandlerHarRolle(consumerId, rolle)) {
            throw ForbiddenException("Saksbehandler $consumerId har ikke rollen $rolle")
        }
    }

}

private fun mapOppgave(oppgave: Oppgave) = mapOfNotNullOrEmpty(
        "oppgaveid" to oppgave.oppgaveId,
        "henvendelseid" to oppgave.henvendelseId,
        "fødselsnummer" to oppgave.fnr
)

private fun getTemagruppefraRequest(temagruppe: String) =
        try {
            Temagruppe.valueOf(temagruppe)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Ugyldig temagruppe: $temagruppe")
        }

private fun handterRuntimeFeil(exception: RuntimeException): RuntimeException {
    logger.error("Feil ved legging av oppgave tilbake til GSAK", exception)
    createEvent("hendelse.oppgavecontroller.leggtilbake.runtime-exception").report()
    return exception
}
