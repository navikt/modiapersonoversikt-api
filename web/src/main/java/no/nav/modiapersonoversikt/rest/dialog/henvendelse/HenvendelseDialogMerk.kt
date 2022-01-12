package no.nav.modiapersonoversikt.rest.dialog.henvendelse

import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.api.service.OppgaveBehandlingService
import no.nav.modiapersonoversikt.rest.dialog.apis.*
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.ws.rs.NotSupportedException

class HenvendelseDialogMerk(
    private val behandleHenvendelsePortType: BehandleHenvendelsePortType,
    private val oppgaveBehandlingService: OppgaveBehandlingService,
    private val tilgangskontroll: Tilgangskontroll
) : DialogMerkApi {
    override fun merkSomFeilsendt(request: FeilmerkRequest): ResponseEntity<Void> {
        behandleHenvendelsePortType.oppdaterTilKassering(request.behandlingsidListe)
        return ResponseEntity(HttpStatus.OK)
    }

    override fun merkSomBidrag(request: BidragRequest): ResponseEntity<Void> {
        behandleHenvendelsePortType.knyttBehandlingskjedeTilTema(request.eldsteMeldingTraadId, "BID")
        return ResponseEntity(HttpStatus.OK)
    }

    override fun merkSomKontorsperret(request: KontorsperretRequest): ResponseEntity<Void> {
        behandleHenvendelsePortType.oppdaterKontorsperre(request.enhet, request.meldingsidListe)
        return ResponseEntity(HttpStatus.OK)
    }

    override fun sendTilSladding(request: SendTilSladdingRequest): ResponseEntity<Void> {
        throw NotSupportedException("Operasjonen er kun støttet av SF")
    }

    override fun avsluttUtenSvar(request: AvsluttUtenSvarRequest): ResponseEntity<Void> {
        behandleHenvendelsePortType.ferdigstillUtenSvar(request.eldsteMeldingTraadId, request.saksbehandlerValgtEnhet)
        oppgaveBehandlingService.ferdigstillOppgaveIGsak(request.eldsteMeldingOppgaveId, Optional.empty(), request.saksbehandlerValgtEnhet)
        return ResponseEntity(HttpStatus.OK)
    }

    override fun tvungenFerdigstill(request: TvungenFerdigstillRequest): ResponseEntity<Void> {
        behandleHenvendelsePortType.ferdigstillUtenSvar(request.eldsteMeldingTraadId, request.saksbehandlerValgtEnhet)
        oppgaveBehandlingService.ferdigstillOppgaveIGsak(request.eldsteMeldingOppgaveId, Optional.empty(), request.saksbehandlerValgtEnhet, request.beskrivelse)
        return ResponseEntity(HttpStatus.OK)
    }

    override fun avsluttGosysOppgave(@RequestBody request: FerdigstillOppgaveRequest): ResponseEntity<Void> {
        oppgaveBehandlingService.ferdigstillOppgaveIGsak(request.oppgaveid, Optional.empty(), request.saksbehandlerValgtEnhet, request.beskrivelse)
        return ResponseEntity(HttpStatus.OK)
    }

    override fun slettBehandlingskjede(request: FeilmerkRequest): ResponseEntity<Void> {
        behandleHenvendelsePortType.markerTraadForHasteKassering(request.behandlingsidListe)
        return ResponseEntity(HttpStatus.OK)
    }

    override fun kanSlette(): ResponseEntity<Boolean> {
        val godkjenteSaksbehandlere = tilgangskontroll.context().hentSaksbehandlereMedTilgangTilHastekassering()
        val saksbehandlerId = AuthContextUtils.requireIdent().uppercase()
        return ResponseEntity(godkjenteSaksbehandlere.contains(saksbehandlerId), HttpStatus.OK)
    }
}
