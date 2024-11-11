package no.nav.modiapersonoversikt.rest.dialog

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.*
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.rest.dialog.apis.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@Deprecated("Use dialog-controller-v2")
@RestController
@RequestMapping("/rest/dialog/{fnr}")
class DialogController
    @Autowired
    constructor(
        private val tilgangskontroll: Tilgangskontroll,
        private val dialogapi: DialogApi,
    ) {
        @GetMapping("/meldinger")
        fun hentMeldinger(
            @PathVariable("fnr") fnr: String,
            @RequestParam(value = "enhet") enhet: String,
        ): List<TraadDTO> =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnr)))
                .get(Audit.describe(READ, Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                    dialogapi.hentMeldinger(fnr, enhet)
                }

        @PostMapping("/sendmelding")
        fun sendMeldinger(
            @PathVariable("fnr") fnr: String,
            @RequestBody meldingRequest: SendMeldingRequest,
        ): TraadDTO {
            val auditIdentifier =
                arrayOf(
                    AuditIdentifier.FNR to fnr,
                    AuditIdentifier.BEHANDLING_ID to meldingRequest.behandlingsId,
                    AuditIdentifier.OPPGAVE_ID to meldingRequest.oppgaveId,
                )
            return tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnr)))
                .get(Audit.describe(CREATE, Person.Henvendelse.Opprettet, *auditIdentifier)) {
                    dialogapi.sendMelding(fnr, meldingRequest)
                }
        }

        @PostMapping("/fortsett/opprett")
        fun startFortsettDialog(
            @PathVariable("fnr") fnr: String,
            @RequestHeader(value = "Ignore-Conflict", required = false) ignorerConflict: Boolean?,
            @RequestBody opprettHenvendelseRequest: OpprettHenvendelseRequest,
        ): FortsettDialogDTO {
            val auditIdentifier =
                arrayOf(
                    AuditIdentifier.FNR to fnr,
                    AuditIdentifier.TRAAD_ID to opprettHenvendelseRequest.traadId,
                )
            return tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnr)))
                .get(Audit.describe(CREATE, Person.Henvendelse.Opprettet, *auditIdentifier)) {
                    dialogapi.startFortsettDialog(fnr, ignorerConflict, opprettHenvendelseRequest)
                }
        }

        @PostMapping("/fortsett/ferdigstill")
        fun sendFortsettDialog(
            @PathVariable("fnr") fnr: String,
            @RequestBody meldingRequest: SendMeldingRequest,
        ): TraadDTO {
            val auditIdentifier =
                arrayOf(
                    AuditIdentifier.FNR to fnr,
                    AuditIdentifier.BEHANDLING_ID to meldingRequest.behandlingsId,
                    AuditIdentifier.OPPGAVE_ID to meldingRequest.oppgaveId,
                )
            return tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnr)))
                .get(Audit.describe(UPDATE, Person.Henvendelse.Ferdigstill, *auditIdentifier)) {
                    dialogapi.fortsettPaEksisterendeDialog(fnr, meldingRequest)
                }
        }
    }
