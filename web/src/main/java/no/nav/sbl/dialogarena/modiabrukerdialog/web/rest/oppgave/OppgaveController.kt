package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppgave

import no.nav.common.auth.subject.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.LeggTilbakeOppgaveIGsakRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.CookieUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.AuditResources.Person.Henvendelse
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.mapOfNotNullOrEmpty
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveService
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.*
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response

private val logger = LoggerFactory.getLogger(OppgaveController::class.java)
private const val HENT_OPPGAVE_ROLLE = "0000-GA-BD06_HentOppgave"
private const val AARSAK_PREFIX = "Oppgave lagt tilbake. Årsak: "

@Path("/oppgaver")
@Produces(APPLICATION_JSON)
class OppgaveController @Inject constructor(
        private val oppgaveBehandlingService: OppgaveBehandlingService,
        private val plukkOppgaveService: PlukkOppgaveService,
        private val ldapService: LDAPService,
        private val henvendelseUtsendingService: HenvendelseUtsendingService,
        private val tilgangkontroll: Tilgangskontroll
) {

    @POST
    @Path("/legg-tilbake")
    fun leggTilbake(@Context httpRequest: HttpServletRequest, request: LeggTilbakeRequest): Response {
        return tilgangkontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.describe(UPDATE, Henvendelse.Oppgave.LeggTilbake, AuditIdentifier.OPPGAVE_ID to request.oppgaveId)) {
                    val valgtEnhet = RestUtils.hentValgtEnhet(httpRequest)
                    val leggTilbakeOppgaveIGsakRequest = lagLeggTilbakeRequest(request, valgtEnhet)

                    try {
                        oppgaveBehandlingService.leggTilbakeOppgaveIGsak(leggTilbakeOppgaveIGsakRequest)
                        if (request.type == LeggTilbakeAarsak.FeilTema) {
                            henvendelseUtsendingService.oppdaterTemagruppe(request.traadId, request.temagruppe.toString())
                        }
                    } catch (exception: RuntimeException) {
                        throw handterRuntimeFeil(exception)
                    }

                    Response.ok("{\"message\": \"Success\"}").build()
                }
    }

    private fun lagLeggTilbakeRequest(request: LeggTilbakeRequest, valgtEnhet: String): LeggTilbakeOppgaveIGsakRequest? {
        require(request.oppgaveId != null)
        val baseRequest = LeggTilbakeOppgaveIGsakRequest()
                .withOppgaveId(request.oppgaveId)
                .withSaksbehandlersValgteEnhet(valgtEnhet)
        return when (request.type) {
            LeggTilbakeAarsak.Innhabil -> baseRequest
                    .withBeskrivelse(AARSAK_PREFIX + "inhabil")
            LeggTilbakeAarsak.FeilTema -> {
                require(request.temagruppe != null)
                return baseRequest
                        .withBeskrivelse(AARSAK_PREFIX + "feil temagruppe")
                        .withTemagruppe(request.temagruppe)
            }
            LeggTilbakeAarsak.AnnenAarsak -> {
                require(request.beskrivelse != null)
                return baseRequest
                        .withBeskrivelse(AARSAK_PREFIX + request.beskrivelse)
            }
        }
    }

    @POST
    @Path("/plukk/{temagruppe}")
    fun plukkOppgaver(@PathParam("temagruppe") temagruppe: String, @Context httpRequest: HttpServletRequest): List<Map<String, String>> {
        return tilgangkontroll
                .check(Policies.tilgangTilModia)
                .check(Policies.kanPlukkeOppgave)
                .get(Audit.describe(READ, Henvendelse.Oppgave.Plukk, AuditIdentifier.TEMAGRUPPE to temagruppe)) {
                    val tildelteOppgaver = oppgaveBehandlingService.finnTildelteOppgaverIGsak()
                    if (tildelteOppgaver.isNotEmpty()) {
                        tildelteOppgaver
                    } else {
                        plukkOppgaveService
                                .also { verifiserTilgang(HENT_OPPGAVE_ROLLE) }
                                .plukkOppgaver(Temagruppe.valueOf(temagruppe.toUpperCase()),
                                        CookieUtil.getSaksbehandlersValgteEnhet(httpRequest))
                    }
                }.map { mapOppgave(it) }
    }

    @GET
    @Path("/tildelt")
    fun finnTildelte() =
            tilgangkontroll
                    .check(Policies.tilgangTilModia)
                    .get(Audit.describe(READ, Henvendelse.Oppgave.Tildelte)) {
                        oppgaveBehandlingService.finnTildelteOppgaverIGsak()
                                .map { mapOppgave(it) }
                    }

    private fun verifiserTilgang(rolle: String) {
        val consumerId = SubjectHandler.getIdent().get()
        if (!ldapService.saksbehandlerHarRolle(consumerId, rolle)) {
            throw ForbiddenException("Saksbehandler $consumerId har ikke rollen $rolle")
        }
    }

}

private fun mapOppgave(oppgave: Oppgave) = mapOfNotNullOrEmpty(
        "oppgaveId" to oppgave.oppgaveId,
        "traadId" to oppgave.henvendelseId,
        "fødselsnummer" to oppgave.fnr
)

private fun handterRuntimeFeil(exception: RuntimeException): RuntimeException {
    logger.error("Feil ved legging av oppgave tilbake til GSAK", exception)
    return exception
}

enum class LeggTilbakeAarsak {
    Innhabil,
    FeilTema,
    AnnenAarsak
}

data class LeggTilbakeRequest(
        val type: LeggTilbakeAarsak,
        val oppgaveId: String,
        val temagruppe: Temagruppe?,
        val beskrivelse: String?,
        val traadId: String?
)
