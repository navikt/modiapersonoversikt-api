package no.nav.sbl.dialogarena.modiabrukerdialog.api.service

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave

import java.time.LocalDate

interface OppgaveRestClient {
    fun opprettOppgave(request: OppgaveRequest): OppgaveResponse
    fun hentOppgave(id: String): Oppgave
}

data class OppgaveResponse(
        val id: String
)

data class  OppgaveRequest(
        val fnr: String,
        val behandlesAvApplikasjon: String,
        val beskrivelse: String,
        val temagruppe: String,
        val tema: String,
        val oppgavetype: String,
        val behandlingstype: String,
        val prioritet: String,
        val underkategoriKode: String?,
        val opprettetavenhetsnummer: String,
        val oppgaveFrist: LocalDate
)
