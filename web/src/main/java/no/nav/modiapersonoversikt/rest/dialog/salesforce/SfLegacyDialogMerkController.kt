package no.nav.modiapersonoversikt.rest.dialog.salesforce

import no.nav.modiapersonoversikt.rest.dialog.apis.*
import no.nav.modiapersonoversikt.service.oppgavebehandling.OppgaveBehandlingService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class SfLegacyDialogMerkController(
    private val sfHenvendelseService: SfHenvendelseService,
    private val oppgaveBehandlingService: OppgaveBehandlingService,
) : DialogMerkApi {
    override fun merkSomFeilsendt(request: MerkSomFeilsendtRequest): ResponseEntity<Void> {
        require(request.behandlingsidListe.size == 1) {
            "Man forventer en enkelt kjedeId"
        }
        sfHenvendelseService.merkSomFeilsendt(request.behandlingsidListe.first())
        return ResponseEntity(HttpStatus.OK)
    }

    override fun sendTilSladding(request: SendTilSladdingRequest): ResponseEntity<Void> {
        sfHenvendelseService.sendTilSladding(request.traadId, request.arsak, request.meldingId)
        return ResponseEntity(HttpStatus.OK)
    }

    override fun hentSladdeArsaker(kjedeId: String): List<String> {
        return sfHenvendelseService.hentSladdeArsaker(kjedeId)
    }

    override fun lukkTraad(request: LukkTraadRequest): ResponseEntity<Void> {
        sfHenvendelseService.lukkTraad(request.traadId)
        if (request.oppgaveId != null && !oppgaveBehandlingService.oppgaveErFerdigstilt(request.oppgaveId)) {
            oppgaveBehandlingService.ferdigstillOppgaveIGsak(
                request.oppgaveId,
                Optional.empty(),
                request.saksbehandlerValgtEnhet,
                "Dialog avsluttet fra modiapersonoversikt.",
            )
        }
        return ResponseEntity(HttpStatus.OK)
    }

    override fun avsluttGosysOppgave(request: AvsluttGosysOppgaveRequest): ResponseEntity<Void> {
        oppgaveBehandlingService.ferdigstillOppgaveIGsak(
            request.oppgaveid,
            Optional.empty(),
            request.saksbehandlerValgtEnhet,
            request.beskrivelse,
        )
        return ResponseEntity(HttpStatus.OK)
    }
}
