package no.nav.sbl.dialogarena.modiabrukerdialog.api.service

import java.time.LocalDate

interface OppgaveRestClient {
    fun opprettOppgave(requestOpprett: OpprettOppgaveRequest): OpprettOppgaveResponse
    fun opprettSkjermetOppgave(requestOpprett: OpprettOppgaveRequest) : OpprettOppgaveResponse
}

data class OpprettOppgaveResponse(
        val id: String
)

data class  OpprettOppgaveRequest(
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
        val oppgaveFrist: LocalDate,
        val valgtEnhetsId: String,
        val behandlingskjedeId: String,
        val dagerFrist : Int,
        val ansvarligEnhetId: String,
        val ansvarligIdent: String?



)

