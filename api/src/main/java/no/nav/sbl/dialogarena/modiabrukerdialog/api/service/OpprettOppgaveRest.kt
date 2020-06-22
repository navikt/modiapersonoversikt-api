package no.nav.sbl.dialogarena.modiabrukerdialog.api.service

import java.time.LocalDate

interface OppgaveRestClient {
    fun opprettOppgave(request: OppgaveRequest) : OppgaveResponse
}

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
data class OppgaveResponse (
        val id: String
)