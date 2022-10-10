package no.nav.modiapersonoversikt.rest.dialog

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.*
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person.Henvendelse
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
    fun merkSomFeilsendt(@RequestBody request: MerkSomFeilsendtRequest): ResponseEntity<Void> {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to request.fnr,
            AuditIdentifier.BEHANDLING_ID to request.behandlingsidListe.joinToString(", ")
        )
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(request.fnr)))
            .check(Policies.henvendelseTilhorerBruker(Fnr(request.fnr), request.behandlingsidListe.first()))
            .get(Audit.describe(UPDATE, Henvendelse.Merk.Feilsendt, *auditIdentifier)) {
                dialogMerkApi.merkSomFeilsendt(request)
            }
    }

    @PostMapping("/sladding")
    fun sendTilSladding(@RequestBody request: SendTilSladdingRequest): ResponseEntity<Void> {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to request.fnr,
            AuditIdentifier.BEHANDLING_ID to request.traadId
        )
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(request.fnr)))
            .check(Policies.henvendelseTilhorerBruker(Fnr(request.fnr), request.traadId))
            .get(Audit.describe(UPDATE, Henvendelse.Merk.Sladding, *auditIdentifier)) {
                dialogMerkApi.sendTilSladding(request)
            }
    }

    @GetMapping("/sladdearsaker/{kjedeid}")
    fun hentSladdeArsaker(@PathVariable("kjedeid") kjedeId: String): List<String> {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(READ, Henvendelse.Merk.SladdeArsaker, AuditIdentifier.TRAAD_ID to kjedeId)) {
                dialogMerkApi.hentSladdeArsaker(kjedeId)
            }
    }

    @PostMapping("/lukk-traad")
    fun lukkTraad(@RequestBody request: LukkTraadRequest): ResponseEntity<Void> {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to request.fnr,
            AuditIdentifier.TRAAD_ID to request.traadId,
            AuditIdentifier.OPPGAVE_ID to request.oppgaveId
        )
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(request.fnr)))
            .check(Policies.henvendelseTilhorerBruker(Fnr(request.fnr), request.traadId))
            .get(Audit.describe(UPDATE, Henvendelse.Merk.Lukk, *auditIdentifier)) {
                dialogMerkApi.lukkTraad(request)
            }
    }

    @PostMapping("/avsluttgosysoppgave")
    fun avsluttGosysOppgave(@RequestBody request: AvsluttGosysOppgaveRequest): ResponseEntity<Void> {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to request.fnr,
            AuditIdentifier.OPPGAVE_ID to request.oppgaveid
        )
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(request.fnr)))
            .get(Audit.describe(UPDATE, Henvendelse.Oppgave.Avslutt, *auditIdentifier)) {
                dialogMerkApi.avsluttGosysOppgave(request)
            }
    }
}
