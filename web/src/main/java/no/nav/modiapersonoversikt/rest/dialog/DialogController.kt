package no.nav.modiapersonoversikt.rest.dialog

import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.*
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.rest.dialog.apis.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/rest/dialog/{fnr}")
class DialogController @Autowired constructor(
    private val tilgangskontroll: Tilgangskontroll,
    private val dialogapi: DialogApi
) {
    @GetMapping("/meldinger")
    fun hentMeldinger(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestParam(value = "enhet", required = false) enhet: String?
    ): List<TraadDTO> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(READ, Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                dialogapi.hentMeldinger(request, fnr, enhet)
            }
    }

    @PostMapping("/sendreferat")
    fun sendMelding(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody referatRequest: SendReferatRequest
    ): TraadDTO {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(CREATE, Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                dialogapi.sendMelding(request, fnr, referatRequest)
            }
    }

    @PostMapping("/sendsporsmal")
    fun sendSporsmal(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody sporsmalsRequest: SendSporsmalRequest
    ): TraadDTO {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(CREATE, Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                dialogapi.sendSporsmal(request, fnr, sporsmalsRequest)
            }
    }

    @PostMapping("/sendinfomelding")
    fun sendInfomelding(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody infomeldingRequest: InfomeldingRequest
    ): TraadDTO {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(CREATE, Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                dialogapi.sendInfomelding(request, fnr, infomeldingRequest)
            }
    }

    @PostMapping("/fortsett/opprett")
    fun startFortsettDialog(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestHeader(value = "Ignore-Conflict", required = false) ignorerConflict: Boolean?,
        @RequestBody opprettHenvendelseRequest: OpprettHenvendelseRequest
    ): FortsettDialogDTO {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to fnr,
            AuditIdentifier.TRAAD_ID to opprettHenvendelseRequest.traadId
        )
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(CREATE, Person.Henvendelse.Opprettet, *auditIdentifier)) {
                dialogapi.startFortsettDialog(request, fnr, ignorerConflict, opprettHenvendelseRequest)
            }
    }

    @PostMapping("/fortsett/ferdigstill")
    fun sendFortsettDialog(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody fortsettDialogRequest: FortsettDialogRequest
    ): TraadDTO {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to fnr,
            AuditIdentifier.BEHANDLING_ID to fortsettDialogRequest.behandlingsId,
            AuditIdentifier.OPPGAVE_ID to fortsettDialogRequest.oppgaveId
        )
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(UPDATE, Person.Henvendelse.Ferdigstill, *auditIdentifier)) {
                dialogapi.sendFortsettDialog(request, fnr, fortsettDialogRequest)
            }
    }

    @PostMapping("slaasammen")
    fun slaaSammenTraader(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody slaaSammenRequest: SlaaSammenRequest
    ): Map<String, Any?> {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to fnr,
            AuditIdentifier.TRAAD_ID to slaaSammenRequest.traader.joinToString(" ") {
                "${it.traadId}:${it.oppgaveId}"
            }
        )
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(UPDATE, Person.Henvendelse.SlaSammen, *auditIdentifier)) {
                dialogapi.slaaSammenTraader(request, fnr, slaaSammenRequest)
            }
    }
}
