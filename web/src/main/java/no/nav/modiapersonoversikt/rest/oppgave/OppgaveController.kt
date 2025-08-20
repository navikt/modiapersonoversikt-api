package no.nav.modiapersonoversikt.rest.oppgave

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.READ
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person.Henvendelse
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.rest.common.FnrRequest
import no.nav.modiapersonoversikt.service.oppgavebehandling.Oppgave
import no.nav.modiapersonoversikt.service.oppgavebehandling.OppgaveBehandlingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/oppgaver")
class OppgaveController
    @Autowired
    constructor(
        private val oppgaveBehandlingService: OppgaveBehandlingService,
        private val tilgangkontroll: Tilgangskontroll,
    ) {
        @Deprecated("Ønsker å bytte om til å bare hente tildelte oppgaver gitt en person")
        @GetMapping("/tildelt")
        fun finnTildelte() =
            tilgangkontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.describe(READ, Henvendelse.Oppgave.Tildelte)) {
                    oppgaveBehandlingService
                        .finnTildelteOppgaverIGsak()
                        .map { mapOppgave(it) }
                }

        @PostMapping("/tildelt")
        fun finnTildelte(
            @RequestBody fnrRequest: FnrRequest,
        ): List<OppgaveDTO> =
            tilgangkontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.describe(READ, Henvendelse.Oppgave.Tildelte)) {
                    oppgaveBehandlingService
                        .finnTildelteOppgaverIGsak(fnrRequest.fnr)
                        .map { mapOppgave(it) }
                }

        @GetMapping("/oppgavedata/{oppgaveId}")
        fun getOppgaveData(
            @PathVariable("oppgaveId") oppgaveId: String,
        ): OppgaveDTO =
            tilgangkontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.describe(READ, Henvendelse.Oppgave.Metadata, AuditIdentifier.OPPGAVE_ID to oppgaveId)) {
                    mapOppgave(oppgaveBehandlingService.hentOppgave(oppgaveId))
                }
    }

data class OppgaveDTO(
    val oppgaveId: String,
    val traadId: String?,
    val fødselsnummer: String?,
    val erSTOOppgave: Boolean,
    val tildeltEnhetsnr: String,
    val tema: String,
    val temagruppe: String? = null,
    val oppgavetype: String,
    val prioritet: String,
    val status: String,
    val aktivDato: LocalDate,
    val endretAvEnhetsnr: String? = null,
    val opprettetAvEnhetsnr: String? = null,
    val saksreferanse: String? = null,
    val beskrivelse: String? = null,
    val fristFerdigstillelse: LocalDate? = null,
    val opprettetTidspunkt: LocalDateTime? = null,
)

private fun mapOppgave(oppgave: Oppgave) =
    OppgaveDTO(
        oppgaveId = oppgave.oppgaveId,
        traadId = oppgave.henvendelseId,
        fødselsnummer = oppgave.fnr,
        erSTOOppgave = oppgave.erSTOOppgave,
        tildeltEnhetsnr = oppgave.tildeltEnhetsnr,
        tema = oppgave.tema,
        temagruppe = oppgave.oppgavetype,
        oppgavetype = oppgave.oppgavetype,
        prioritet = oppgave.prioritet,
        status = oppgave.status,
        aktivDato = oppgave.aktivDato,
        fristFerdigstillelse = oppgave.fristFerdigstillelse,
        endretAvEnhetsnr = oppgave.endretAvEnhetsnr,
        opprettetAvEnhetsnr = oppgave.opprettetAvEnhetsnr,
        saksreferanse = oppgave.saksreferanse,
        beskrivelse = oppgave.beskrivelse,
        opprettetTidspunkt = oppgave.opprettetTidspunkt,
    )
