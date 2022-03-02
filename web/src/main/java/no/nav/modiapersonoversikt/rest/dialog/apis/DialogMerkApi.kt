
package no.nav.modiapersonoversikt.rest.dialog.apis
import org.springframework.http.ResponseEntity

interface DialogMerkApi {
    fun merkSomFeilsendt(request: MerkSomFeilsendtRequest): ResponseEntity<Void>
    fun sendTilSladding(request: SendTilSladdingRequest): ResponseEntity<Void>
    fun avsluttGosysOppgave(request: AvsluttGosysOppgaveRequest): ResponseEntity<Void>
    fun lukkTraad(request: LukkTraadRequest): ResponseEntity<Void>
}

data class MerkSomFeilsendtRequest(
    val fnr: String,
    val behandlingsidListe: List<String>
)

data class SendTilSladdingRequest(
    val fnr: String,
    val traadId: String
)

data class AvsluttGosysOppgaveRequest(
    val fnr: String,
    val saksbehandlerValgtEnhet: String,
    val oppgaveid: String,
    val beskrivelse: String
)

data class LukkTraadRequest(
    val fnr: String,
    val saksbehandlerValgtEnhet: String,
    val traadId: String,
    val oppgaveId: String?
)
