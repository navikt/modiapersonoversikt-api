package no.nav.sbl.dialogarena.modiabrukerdialog.api.service

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave

interface OppgaveRestClient {
    fun opprettOppgave(request: OppgaveRequest): OppgaveResponse
    fun hentOppgave(id: String): Oppgave
    fun oppgaveErFerdigstilt(oppgaveid: String): Boolean
}