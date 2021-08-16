
package no.nav.modiapersonoversikt.rest.dialog.apis
import org.springframework.http.ResponseEntity

interface DialogMerkApi {
    fun merkSomFeilsendt(request: FeilmerkRequest): ResponseEntity<Void>
    fun merkSomBidrag(request: BidragRequest): ResponseEntity<Void>
    fun merkSomKontorsperret(request: KontorsperretRequest): ResponseEntity<Void>
    fun avsluttUtenSvar(request: AvsluttUtenSvarRequest): ResponseEntity<Void>
    fun tvungenFerdigstill(request: TvungenFerdigstillRequest): ResponseEntity<Void>
    fun avsluttGosysOppgave(request: FerdigstillOppgaveRequest): ResponseEntity<Void>
    fun slettBehandlingskjede(request: FeilmerkRequest): ResponseEntity<Void>
    fun kanSlette(): ResponseEntity<Boolean>
}

data class FeilmerkRequest(val fnr: String, val behandlingsidListe: List<String>)

data class BidragRequest(val fnr: String, val eldsteMeldingTraadId: String)

data class KontorsperretRequest(
    val fnr: String,
    val enhet: String,
    val meldingsidListe: List<String>
)

data class AvsluttUtenSvarRequest(
    val fnr: String,
    val saksbehandlerValgtEnhet: String,
    val eldsteMeldingTraadId: String,
    val eldsteMeldingOppgaveId: String
)

data class TvungenFerdigstillRequest(
    val fnr: String,
    val saksbehandlerValgtEnhet: String,
    val eldsteMeldingTraadId: String,
    val eldsteMeldingOppgaveId: String,
    val beskrivelse: String
)

data class FerdigstillOppgaveRequest(
    val fnr: String,
    val oppgaveid: String,
    val beskrivelse: String,
    val saksbehandlerValgtEnhet: String
)
