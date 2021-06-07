package no.nav.modiapersonoversikt.rest.oppgave

import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.READ
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.UPDATE
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person.Henvendelse
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.api.domain.Oppgave
import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe
import no.nav.modiapersonoversikt.legacy.api.service.HenvendelseUtsendingService
import no.nav.modiapersonoversikt.legacy.api.service.LeggTilbakeOppgaveIGsakRequest
import no.nav.modiapersonoversikt.legacy.api.service.OppgaveBehandlingService
import no.nav.modiapersonoversikt.legacy.api.utils.RestUtils
import no.nav.modiapersonoversikt.service.plukkoppgave.PlukkOppgaveService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

private val logger = LoggerFactory.getLogger(OppgaveController::class.java)
private const val AARSAK_PREFIX = "Oppgave lagt tilbake. Årsak: "

@RestController
@RequestMapping("/rest/oppgaver")
class OppgaveController @Autowired constructor(
    private val oppgaveBehandlingService: OppgaveBehandlingService,
    private val plukkOppgaveService: PlukkOppgaveService,
    private val henvendelseUtsendingService: HenvendelseUtsendingService,
    private val tilgangkontroll: Tilgangskontroll
) {

    @PostMapping("/legg-tilbake")
    fun leggTilbake(httpRequest: HttpServletRequest, @RequestBody request: LeggTilbakeRequest): ResponseEntity<Void> {
        return tilgangkontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(UPDATE, Henvendelse.Oppgave.LeggTilbake, AuditIdentifier.OPPGAVE_ID to request.oppgaveId)) {
                val valgtEnhet = RestUtils.hentValgtEnhet(request.enhet, httpRequest)
                val leggTilbakeOppgaveIGsakRequest = lagLeggTilbakeRequest(request, valgtEnhet)

                try {
                    oppgaveBehandlingService.leggTilbakeOppgaveIGsak(leggTilbakeOppgaveIGsakRequest)
                    if (request.type == LeggTilbakeAarsak.FeilTema) {
                        henvendelseUtsendingService.oppdaterTemagruppe(request.traadId, request.temagruppe.toString())
                    }
                } catch (exception: RuntimeException) {
                    throw handterRuntimeFeil(exception)
                }

                ResponseEntity(HttpStatus.OK)
            }
    }

    @PostMapping("/plukk/{temagruppe}")
    fun plukkOppgaver(@PathVariable("temagruppe") temagruppe: String, @RequestParam(value = "enhet", required = false) enhet: String?, httpRequest: HttpServletRequest): List<OppgaveDTO> {
        return tilgangkontroll
            .check(Policies.tilgangTilModia)
            .check(Policies.kanPlukkeOppgave)
            .get(Audit.describe(READ, Henvendelse.Oppgave.Plukk, AuditIdentifier.TEMAGRUPPE to temagruppe)) {
                val tildelteOppgaver = oppgaveBehandlingService.finnTildelteKNAOppgaverIGsak()
                if (tildelteOppgaver.any { it.erSTOOppgave }) {
                    tildelteOppgaver
                } else {
                    plukkOppgaveService
                        .plukkOppgaver(
                            Temagruppe.valueOf(temagruppe.toUpperCase()),
                            RestUtils.hentValgtEnhet(enhet, httpRequest)
                        )
                }
            }.map { mapOppgave(it) }
    }

    @Deprecated("Ønsker å bytte om til å bare hente tildelte oppgaver gitt en person")
    @GetMapping("/tildelt")
    fun finnTildelte() =
        tilgangkontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(READ, Henvendelse.Oppgave.Tildelte)) {
                oppgaveBehandlingService.finnTildelteOppgaverIGsak()
                    .map { mapOppgave(it) }
            }

    @GetMapping("/tildelt/{fnr}")
    fun finnTildelte(@PathVariable("fnr") fnr: String): List<OppgaveDTO> =
        tilgangkontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(READ, Henvendelse.Oppgave.Tildelte)) {
                oppgaveBehandlingService.finnTildelteOppgaverIGsak(fnr)
                    .map { mapOppgave(it) }
            }

    @GetMapping("/oppgavedata/{oppgaveId}")
    fun getOppgaveData(@PathVariable("oppgaveId") oppgaveId: String): OppgaveDTO =
        tilgangkontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(READ, Henvendelse.Oppgave.Metadata, AuditIdentifier.OPPGAVE_ID to oppgaveId)) {
                mapOppgave(oppgaveBehandlingService.hentOppgave(oppgaveId))
            }

    private fun lagLeggTilbakeRequest(request: LeggTilbakeRequest, valgtEnhet: String): LeggTilbakeOppgaveIGsakRequest? {
        require(request.oppgaveId != null)
        val baseRequest = LeggTilbakeOppgaveIGsakRequest()
            .withOppgaveId(request.oppgaveId)
            .withSaksbehandlersValgteEnhet(valgtEnhet)
        return when (request.type) {
            LeggTilbakeAarsak.Innhabil ->
                baseRequest
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
}

data class OppgaveDTO(
    val oppgaveId: String,
    val traadId: String?,
    val fødselsnummer: String?,
    val erSTOOppgave: Boolean
)
private fun mapOppgave(oppgave: Oppgave) = OppgaveDTO(
    oppgave.oppgaveId,
    oppgave.henvendelseId,
    oppgave.fnr,
    oppgave.erSTOOppgave
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
    val enhet: String?,
    val type: LeggTilbakeAarsak,
    val oppgaveId: String,
    val temagruppe: Temagruppe?,
    val beskrivelse: String?,
    val traadId: String?
)
