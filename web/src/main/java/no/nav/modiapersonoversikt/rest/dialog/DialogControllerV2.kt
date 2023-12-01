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

@RestController
@RequestMapping("/rest/dialog")
class DialogControllerV2 @Autowired constructor(
    private val tilgangskontroll: Tilgangskontroll,
    private val dialogapi: DialogApi,
) {
    @PostMapping("/meldinger")
    fun hentMeldinger(
        @RequestParam(value = "enhet") enhet: String,
        @RequestBody fnr: String,
    ): List<TraadDTO> {
        return tilgangskontroll.check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(READ, Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                dialogapi.hentMeldinger(fnr, enhet)
            }
    }

    @PostMapping("/sendmelding")
    fun sendMeldinger(
        @RequestBody meldingRequest: SendMeldingRequestV2,
    ): TraadDTO {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to meldingRequest.fnr,
            AuditIdentifier.BEHANDLING_ID to meldingRequest.behandlingsId,
            AuditIdentifier.OPPGAVE_ID to meldingRequest.oppgaveId,
        )
        return tilgangskontroll.check(Policies.tilgangTilBruker(Fnr(meldingRequest.fnr)))
            .get(Audit.describe(CREATE, Person.Henvendelse.Opprettet, *auditIdentifier)) {
                dialogapi.sendMelding(meldingRequest.fnr, meldingRequest)
            }
    }

    @PostMapping("/fortsett/opprett")
    fun startFortsettDialog(
        @RequestHeader(value = "Ignore-Conflict", required = false) ignorerConflict: Boolean?,
        @RequestBody opprettHenvendelseRequest: OpprettHenvendelseRequestV2,
    ): FortsettDialogDTO {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to opprettHenvendelseRequest.fnr,
            AuditIdentifier.TRAAD_ID to opprettHenvendelseRequest.traadId,
        )
        return tilgangskontroll.check(Policies.tilgangTilBruker(Fnr(opprettHenvendelseRequest.fnr)))
            .get(Audit.describe(CREATE, Person.Henvendelse.Opprettet, *auditIdentifier)) {
                dialogapi.startFortsettDialog(opprettHenvendelseRequest.fnr, ignorerConflict, opprettHenvendelseRequest)
            }
    }

    @PostMapping("/fortsett/ferdigstill")
    fun sendFortsettDialog(
        @RequestBody meldingRequest: SendMeldingRequestV2,
    ): TraadDTO {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to meldingRequest.fnr,
            AuditIdentifier.BEHANDLING_ID to meldingRequest.behandlingsId,
            AuditIdentifier.OPPGAVE_ID to meldingRequest.oppgaveId,
        )
        return tilgangskontroll.check(Policies.tilgangTilBruker(Fnr(meldingRequest.fnr)))
            .get(Audit.describe(UPDATE, Person.Henvendelse.Ferdigstill, *auditIdentifier)) {
                dialogapi.fortsettPaEksisterendeDialog(meldingRequest.fnr, meldingRequest)
            }
    }
}
