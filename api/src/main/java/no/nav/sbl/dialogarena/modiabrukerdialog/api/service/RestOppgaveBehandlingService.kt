package no.nav.sbl.dialogarena.modiabrukerdialog.api.service

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import java.util.*

interface RestOppgaveBehandlingService {
    fun opprettOppgave(request: OpprettOppgaveRequest) : OpprettOppgaveResponse

    fun opprettSkjermetOppgave(request: OpprettOppgaveRequest): OpprettOppgaveResponse

    fun hentOppgave(oppgaveId: String) : OppgaveRespons

    @Throws(FikkIkkeTilordnet::class)
    fun tilordneOppgaveIGsak(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String)

    fun finnTildelteOppgaverIGsak(): List<Oppgave>

    fun plukkOppgaverFraGsak(temagruppe: Temagruppe, saksbehandlersValgteEnhet: String): List<Oppgave>

    fun ferdigstillOppgaveIGsak(oppgaveId: String, temagruppe: Optional<Temagruppe>, saksbehandlersValgteEnhet: String)

    fun ferdigstillOppgaveIGsak(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String)

    fun ferdigstillOppgaverIGsak(oppgaveId: List<String>, temagruppe: Optional<Temagruppe>, saksbehandlersValgteEnhet: String)

    fun ferdigstillOppgaveIGsak(oppgaveId: String, temagruppe: Optional<Temagruppe>, saksbehandlersValgteEnhet: String, beskrivelse: String)

    fun leggTilbakeOppgaveIGsak(request: LeggTilbakeOppgaveIGsakRequest)

    fun systemLeggTilbakeOppgaveIGsak(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String)

    fun oppgaveErFerdigstilt(oppgaveId: String): Boolean

    class FikkIkkeTilordnet(cause: Throwable) : Exception(cause)
}

data class OppgaveRespons(
        val oppgaveId: String,
        val fnr: String,
        val henvendelseId: String,
        val erSTOOppgave: Boolean
)
