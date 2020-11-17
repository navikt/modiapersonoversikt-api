package no.nav.sbl.dialogarena.modiabrukerdialog.api.service

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe

interface RestOppgaveBehandlingService {
    fun opprettOppgave(request: OpprettOppgaveRequest) : OpprettOppgaveResponse
    fun opprettSkjermetOppgave(request: OpprettOppgaveRequest): OpprettOppgaveResponse

    fun hentOppgave(oppgaveId: String) : OppgaveResponse

    @Throws(FikkIkkeTilordnet::class)
    fun tilordneOppgave(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String)

    fun finnTildelteOppgaver(): List<OppgaveResponse>

    fun plukkOppgaver(temagruppe: Temagruppe, saksbehandlersValgteEnhet: String): List<OppgaveResponse>

    fun systemLeggTilbakeOppgave(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String)

    fun oppgaveErFerdigstilt(oppgaveid: String): Boolean

    class FikkIkkeTilordnet(cause: Throwable) : Exception(cause)
}

data class OppgaveResponse(
        val oppgaveId: String,
        val fnr: String,
        val henvendelseId: String,
        val erSTOOppgave: Boolean
)