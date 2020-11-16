package no.nav.sbl.dialogarena.modiabrukerdialog.api.service

interface RestOppgaveBehandlingService {
    fun opprettOppgave(request: OpprettOppgaveRequest) : OpprettOppgaveResponse
    fun opprettSkjermetOppgave(request: OpprettOppgaveRequest): OpprettOppgaveResponse

    fun hentOppgave(oppgaveId: String) : OppgaveRespons
}

data class OppgaveRespons(
        val oppgaveId: String,
        val fnr: String,
        val henvendelseId: String,
        val erSTOOppgave: Boolean
)