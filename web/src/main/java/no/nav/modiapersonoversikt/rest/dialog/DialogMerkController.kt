package no.nav.modiapersonoversikt.rest.dialog

import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.*
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person.Henvendelse
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.BehandlingsIdTilgangData
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.rest.dialog.apis.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/dialogmerking")
class DialogMerkController @Autowired constructor(
    private val tilgangskontroll: Tilgangskontroll,
    private val dialogMerkApi: DialogMerkApi
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
                dialogMerkApi.merkSomFeilsendt(request)
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
                dialogMerkApi.merkSomBidrag(request)
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
                dialogMerkApi.merkSomKontorsperret(request)
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
                dialogMerkApi.avsluttUtenSvar(request)
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
                dialogMerkApi.tvungenFerdigstill(request)
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
                dialogMerkApi.avsluttGosysOppgave(request)
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
                dialogMerkApi.slettBehandlingskjede(request)
            }
    }

    @GetMapping("/slett")
    fun kanSlette(): ResponseEntity<Boolean> {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.skipAuditLog()) {
                dialogMerkApi.kanSlette()
            }
    }
}
