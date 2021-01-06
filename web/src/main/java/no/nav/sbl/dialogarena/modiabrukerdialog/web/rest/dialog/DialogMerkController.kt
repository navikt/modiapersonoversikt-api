package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog

import no.nav.common.auth.subject.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.BehandlingsIdTilgangData
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.*
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import no.nav.sbl.dialogarena.naudit.AuditResources.Person.Henvendelse
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/dialogmerking")
class DialogMerkController @Autowired constructor(private val behandleHenvendelsePortType: BehandleHenvendelsePortType,
                                               private val oppgaveBehandlingService: OppgaveBehandlingService,
                                               private val tilgangskontroll: Tilgangskontroll
) {

    @PostMapping("/feilsendt")
    fun merkSomFeilsendt(@RequestBody request: FeilmerkRequest): ResponseEntity<Void> {
        val auditIdentifier = arrayOf(
                AuditIdentifier.FNR to request.fnr,
                AuditIdentifier.BEHANDLING_ID to request.behandlingsidListe.joinToString(", ")
        )
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(request.fnr))
                .check(Policies.behandlingsIderTilhorerBruker.with(BehandlingsIdTilgangData(request.fnr, request.behandlingsidListe)))
                .get(Audit.describe(UPDATE, Henvendelse.Merk.Feilsendt, *auditIdentifier)) {
                    behandleHenvendelsePortType.oppdaterTilKassering(request.behandlingsidListe)
                    ResponseEntity(HttpStatus.OK)
                }
    }

    @PostMapping("/bidrag")
    fun merkSomBidrag(@RequestBody request: BidragRequest): ResponseEntity<Void> {
        val auditIdentifier = arrayOf(
                AuditIdentifier.FNR to request.fnr,
                AuditIdentifier.BEHANDLING_ID to request.eldsteMeldingTraadId
        )
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(request.fnr))
                .check(Policies.behandlingsIderTilhorerBruker.with(BehandlingsIdTilgangData(request.fnr, listOf(request.eldsteMeldingTraadId))))
                .get(Audit.describe(UPDATE, Henvendelse.Merk.Bidrag, *auditIdentifier)) {
                    behandleHenvendelsePortType.knyttBehandlingskjedeTilTema(request.eldsteMeldingTraadId, "BID")
                    ResponseEntity(HttpStatus.OK)
                }
    }

    @PostMapping("/kontorsperret")
    fun merkSomKontorsperret(@RequestBody request: KontorsperretRequest): ResponseEntity<Void> {
        val auditIdentifier = arrayOf(
                AuditIdentifier.FNR to request.fnr,
                AuditIdentifier.BEHANDLING_ID to request.meldingsidListe.joinToString(", ")
        )
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(request.fnr))
                .check(Policies.behandlingsIderTilhorerBruker.with(BehandlingsIdTilgangData(request.fnr, request.meldingsidListe)))
                .get(Audit.describe(UPDATE, Henvendelse.Merk.Kontorsperre, *auditIdentifier)) {
                    behandleHenvendelsePortType.oppdaterKontorsperre(request.enhet, request.meldingsidListe)
                    ResponseEntity(HttpStatus.OK)
                }
    }

    @PostMapping("/avslutt")
    fun avsluttUtenSvar(@RequestBody request: AvsluttUtenSvarRequest): ResponseEntity<Void> {
        val auditIdentifier = arrayOf(
                AuditIdentifier.FNR to request.fnr,
                AuditIdentifier.BEHANDLING_ID to request.eldsteMeldingTraadId,
                AuditIdentifier.OPPGAVE_ID to request.eldsteMeldingOppgaveId
        )
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(request.fnr))
                .check(Policies.behandlingsIderTilhorerBruker.with(BehandlingsIdTilgangData(request.fnr, listOf(request.eldsteMeldingTraadId))))
                .get(Audit.describe(UPDATE, Henvendelse.Merk.Avslutt, *auditIdentifier)) {
                    behandleHenvendelsePortType.ferdigstillUtenSvar(request.eldsteMeldingTraadId, request.saksbehandlerValgtEnhet)
                    oppgaveBehandlingService.ferdigstillOppgaveIGsak(request.eldsteMeldingOppgaveId, Optional.empty(), request.saksbehandlerValgtEnhet)
                    ResponseEntity(HttpStatus.OK)
                }
    }

    @PostMapping("/tvungenferdigstill")
    fun tvungenFerdigstill(@RequestBody request: TvungenFerdigstillRequest): ResponseEntity<Void> {
        val auditIdentifier = arrayOf(
                AuditIdentifier.FNR to request.fnr,
                AuditIdentifier.BEHANDLING_ID to request.eldsteMeldingTraadId,
                AuditIdentifier.OPPGAVE_ID to request.eldsteMeldingOppgaveId
        )
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(request.fnr))
                .check(Policies.behandlingsIderTilhorerBruker.with(BehandlingsIdTilgangData(request.fnr, listOf(request.eldsteMeldingTraadId))))
                .get(Audit.describe(UPDATE, Henvendelse.Merk.Avslutt, *auditIdentifier)) {
                    behandleHenvendelsePortType.ferdigstillUtenSvar(request.eldsteMeldingTraadId, request.saksbehandlerValgtEnhet)
                    oppgaveBehandlingService.ferdigstillOppgaveIGsak(request.eldsteMeldingOppgaveId, Optional.empty(), request.saksbehandlerValgtEnhet, request.beskrivelse)
                    ResponseEntity(HttpStatus.OK)
                }
    }

    @PostMapping("/avsluttgosysoppgave")
    fun avsluttGosysOppgave(@RequestBody request: FerdigstillOppgaveRequest): ResponseEntity<Void> {
        val auditIdentifier = arrayOf(
                AuditIdentifier.FNR to request.fnr,
                AuditIdentifier.OPPGAVE_ID to request.oppgaveid
        )
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(request.fnr))
                .get(Audit.describe(UPDATE, Henvendelse.Oppgave.Avslutt, *auditIdentifier)) {
                    oppgaveBehandlingService.ferdigstillOppgaveIGsak(request.oppgaveid, Optional.empty(), request.saksbehandlerValgtEnhet, request.beskrivelse)
                    ResponseEntity(HttpStatus.OK)
                }
    }

    @PostMapping("/slett")
    fun slettBehandlingskjede(@RequestBody request: FeilmerkRequest): ResponseEntity<Void> {
        val auditIdentifier = arrayOf(
                AuditIdentifier.FNR to request.fnr,
                AuditIdentifier.BEHANDLING_ID to request.behandlingsidListe.joinToString(", ")
        )
        return tilgangskontroll
                .check(Policies.kanHastekassere)
                .check(Policies.tilgangTilBruker.with(request.fnr))
                .check(Policies.behandlingsIderTilhorerBruker.with(BehandlingsIdTilgangData(request.fnr, request.behandlingsidListe)))
                .get(Audit.describe(DELETE, Henvendelse.Merk.Slett, *auditIdentifier)) {
                    behandleHenvendelsePortType.markerTraadForHasteKassering(request.behandlingsidListe)
                    ResponseEntity(HttpStatus.OK)
                }
    }

    @GetMapping("/slett")
    fun kanSlette(): ResponseEntity<Boolean> {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.skipAuditLog()) {
                    val godkjenteSaksbehandlere = tilgangskontroll.context().hentSaksbehandlereMedTilgangTilHastekassering()
                    val saksbehandlerId = SubjectHandler.getIdent().map(String::toUpperCase).get()
                    ResponseEntity(godkjenteSaksbehandlere.contains(saksbehandlerId), HttpStatus.OK)
                }
    }

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
