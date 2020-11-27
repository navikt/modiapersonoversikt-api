package no.nav.sbl.dialogarena.modiabrukerdialog.api.service

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe

interface RestOppgaveBehandlingService {
    fun opprettOppgave(request: OpprettOppgaveRequest) : OpprettOppgaveResponse
    fun opprettSkjermetOppgave(request: OpprettOppgaveRequest): OpprettOppgaveResponse

    fun hentOppgave(oppgaveId: String) : OppgaveResponse

    @Throws(FikkIkkeTilordnet::class)
    fun tilordneOppgave(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String)

    fun finnTildelteOppgaver(): List<OppgaveResponse>

    fun ferdigstillOppgave(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String)

    fun ferdigstillOppgaver(oppgaveId: List<String>, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String)

    fun ferdigstillOppgave(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String, beskrivelse: String)

    fun leggTilbakeOppgave(request: LeggTilbakeOppgaveRequest)

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

data class LeggTilbakeOppgaveRequest (
    val saksbehandlersValgteEnhet: String,
    val oppgaveId: String,
    val beskrivelse: String,
    val nyTemagruppe: Temagruppe
) {

}