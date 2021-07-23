package no.nav.modiapersonoversikt.rest.dialog

import no.nav.common.auth.subject.SubjectHandler
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.*
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person.Henvendelse
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.BehandlingsIdTilgangData
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.api.service.OppgaveBehandlingService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.ws.rs.NotSupportedException

@RestController
@RequestMapping("/rest/sf-legacy-dialogmerking")
class SfLegacyDialogMerkController @Autowired constructor(
    private val tilgangskontroll: Tilgangskontroll,
    private val sfHenvendelseService: SfHenvendelseService,
    private val oppgaveBehandlingService: OppgaveBehandlingService
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
                require(request.behandlingsidListe.size == 1) {
                    "Man forventer en enkelt kjedeId"
                }
                sfHenvendelseService.merkSomFeilsendt(request.behandlingsidListe.first())
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
                throw NotSupportedException("Operasjonen er erstattet med standard journalføring")
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
                require(request.meldingsidListe.size == 1) {
                    "Man forventer en enkelt kjedeId"
                }
                sfHenvendelseService.merkSomKontorsperret(request.meldingsidListe.first(), request.enhet)
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
                // TODO kan vi patche avsluttet dato?
                throw NotSupportedException("Operasjonen er ikke støttet av Salesforce")
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
                throw NotSupportedException("Operasjonen er ikke støttet av Salesforce")
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
                require(request.behandlingsidListe.size == 1) {
                    "Man forventer en enkelt kjedeId"
                }
                sfHenvendelseService.merkForHastekassering(request.behandlingsidListe.first())
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
