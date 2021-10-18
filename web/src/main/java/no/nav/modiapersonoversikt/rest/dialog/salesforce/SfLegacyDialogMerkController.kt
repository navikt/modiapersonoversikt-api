package no.nav.modiapersonoversikt.rest.dialog.salesforce

import no.nav.modiapersonoversikt.legacy.api.service.OppgaveBehandlingService
import no.nav.modiapersonoversikt.rest.dialog.apis.*
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*
import javax.ws.rs.NotSupportedException

class SfLegacyDialogMerkController(
    private val sfHenvendelseService: SfHenvendelseService,
    private val oppgaveBehandlingService: OppgaveBehandlingService
) : DialogMerkApi {

    override fun merkSomFeilsendt(request: FeilmerkRequest): ResponseEntity<Void> {
        require(request.behandlingsidListe.size == 1) {
            "Man forventer en enkelt kjedeId"
        }
        sfHenvendelseService.merkSomFeilsendt(request.behandlingsidListe.first())
        return ResponseEntity(HttpStatus.OK)
    }

    override fun merkSomBidrag(request: BidragRequest): ResponseEntity<Void> {
        throw NotSupportedException("Operasjonen er erstattet med standard journalføring")
    }

    override fun merkSomKontorsperret(request: KontorsperretRequest): ResponseEntity<Void> {
        require(request.meldingsidListe.size == 1) {
            "Man forventer en enkelt kjedeId"
        }
        sfHenvendelseService.merkSomKontorsperret(request.meldingsidListe.first(), request.enhet)
        return ResponseEntity(HttpStatus.OK)
    }

    override fun avsluttUtenSvar(request: AvsluttUtenSvarRequest): ResponseEntity<Void> {
        // TODO SF vil det være innafor å merke meldinger på denne måten.
        // Hva skjer evt om vi forsøker å gjøre det med samtalereferat etc?
        sfHenvendelseService.lukkTraad(request.eldsteMeldingTraadId)
        return ResponseEntity(HttpStatus.OK)
    }

    override fun tvungenFerdigstill(request: TvungenFerdigstillRequest): ResponseEntity<Void> {
        throw NotSupportedException("Operasjonen er ikke støttet av Salesforce")
    }

    override fun avsluttGosysOppgave(request: FerdigstillOppgaveRequest): ResponseEntity<Void> {
        oppgaveBehandlingService.ferdigstillOppgaveIGsak(request.oppgaveid, Optional.empty(), request.saksbehandlerValgtEnhet, request.beskrivelse)
        return ResponseEntity(HttpStatus.OK)
    }

    override fun slettBehandlingskjede(request: FeilmerkRequest): ResponseEntity<Void> {
        throw NotSupportedException("Operasjonen må gjøres via Salesforce")
    }

    override fun kanSlette(): ResponseEntity<Boolean> {
        return ResponseEntity(false, HttpStatus.OK)
    }
}
