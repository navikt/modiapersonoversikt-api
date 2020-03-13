package no.nav.sbl.dialogarena.modiabrukerdialog.api.service

import java.time.LocalDate

interface OppgaveRestClient {
    fun opprettOppgave(request: OppgaveRequest) : OppgaveResponse
}

data class  OppgaveRequest(
        val opprettetAvEnhetsnr: String,
        val fnr: String,
        val behandlesAvApplikasjon: String,
        val beskrivelse: String ,
        val temagruppe: String ,
        val tema: String ,
        val oppgavetype : String ,
        val behandlingstype : String ,
        val aktivDato : LocalDate ,
        val fristFerdigstillelse : LocalDate ,
        val prioritet : String,
        val underkategoriKode: String?
)
data class OppgaveResponse (
        val oppgaveid: String
)