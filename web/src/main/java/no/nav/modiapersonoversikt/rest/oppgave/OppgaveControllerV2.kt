package no.nav.modiapersonoversikt.rest.oppgave

import no.nav.modiapersonoversikt.commondomain.FnrRequest
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.READ
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person.Henvendelse
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.oppgavebehandling.Oppgave
import no.nav.modiapersonoversikt.service.oppgavebehandling.OppgaveBehandlingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/v2/oppgaver")
class OppgaveControllerV2 @Autowired constructor(
    private val oppgaveBehandlingService: OppgaveBehandlingService,
    private val tilgangkontroll: Tilgangskontroll
) {

    @Deprecated("Ønsker å bytte om til å bare hente tildelte oppgaver gitt en person")
    @GetMapping("/tildelt")
    fun finnTildelte() =
        tilgangkontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(READ, Henvendelse.Oppgave.Tildelte)) {
                oppgaveBehandlingService.finnTildelteOppgaverIGsak()
                    .map { mapOppgave(it) }
            }

    @PostMapping("/tildelt/{fnr}")
    fun finnTildelte(@RequestBody fnrRequest: FnrRequest): List<OppgaveDTO> =
        tilgangkontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(READ, Henvendelse.Oppgave.Tildelte)) {
                oppgaveBehandlingService.finnTildelteOppgaverIGsak(fnrRequest.fnr)
                    .map { mapOppgave(it) }
            }

    @GetMapping("/oppgavedata/{oppgaveId}")
    fun getOppgaveData(@PathVariable("oppgaveId") oppgaveId: String): OppgaveDTO =
        tilgangkontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(READ, Henvendelse.Oppgave.Metadata, AuditIdentifier.OPPGAVE_ID to oppgaveId)) {
                mapOppgave(oppgaveBehandlingService.hentOppgave(oppgaveId))
            }
}

private fun mapOppgave(oppgave: Oppgave) = OppgaveDTO(
    oppgave.oppgaveId,
    oppgave.henvendelseId,
    oppgave.fnr,
    oppgave.erSTOOppgave
)
