package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.saker.GsakKodeTema
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OpprettOppgaveRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OpprettOppgaveResponse
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OpprettSkjermetOppgaveRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saker.GsakKodeverk
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.BehandlingsIdTilgangData
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.CREATE
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import no.nav.sbl.dialogarena.naudit.AuditResources.Person.Henvendelse
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils.arbeidsdagerFraDatoJava
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

private const val HENVENDELSESTYPE_KODE: String = "DIALOG"

@RestController
@RequestMapping("/rest/dialogoppgave")
class DialogOppgaveController @Autowired constructor(
    private val gsakKodeverk: GsakKodeverk,
    private val oppgavebehandling: OppgaveBehandlingService,
    private val tilgangskontroll: Tilgangskontroll
) {

    @PostMapping("/opprett")
    fun opprettOppgave(@RequestBody request: OpprettOppgaveRequestDTO): OpprettOppgaveResponseDTO {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(request.fnr))
            .check(Policies.behandlingsIderTilhorerBruker.with(BehandlingsIdTilgangData(request.fnr, listOf(request.behandlingskjedeId))))
            .get(Audit.describe(CREATE, Henvendelse.Oppgave.Opprett, AuditIdentifier.FNR to request.fnr, AuditIdentifier.BEHANDLING_ID to request.behandlingskjedeId)) {
                oppgavebehandling.opprettOppgave(request.fromDTO()).toDTO()
            }
    }

    @PostMapping("/opprettskjermetoppgave")
    fun opprettSkjermetOppgave(
        @RequestBody request: OpprettSkjermetOppgaveDTO
    ): OpprettOppgaveResponseDTO {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(CREATE, Henvendelse.Oppgave.Opprett, AuditIdentifier.FNR to request.fnr)) {
                oppgavebehandling.opprettSkjermetOppgave(request.fromDTO()).toDTO()
            }
    }

    @GetMapping("/tema")
    fun hentAlleTema(): List<Map<String, Any?>> {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.skipAuditLog()) {
                val gsakTemaListe = gsakKodeverk.hentTemaListe()
                gsakTemaListe.filter { it.oppgaveTyper.isNotEmpty() }.map {
                    mapOf(
                        *hentGsakKodeTema(it),
                        "oppgavetyper" to hentOppgavetyper(it.oppgaveTyper),
                        "prioriteter" to hentPrioriteter(it.prioriteter),
                        "underkategorier" to hentUnderkategorier(it.underkategorier)
                    )
                }
            }
    }

    private fun kalkulerFrist(temaKode: String, oppgaveTypeKode: String): LocalDate {
        val dagerFrist = gsakKodeverk.hentTemaListe()
            .find { it.kode == temaKode }
            ?.oppgaveTyper
            ?.find { it.kode == oppgaveTypeKode }
            ?.dagerFrist
            ?: 2
        return arbeidsdagerFraDatoJava(dagerFrist, LocalDate.now())
    }

    private fun hentOppgavetyper(oppgavetyper: List<GsakKodeTema.OppgaveType>): List<Map<String, Any?>> =
        oppgavetyper.map {
            mapOf(
                *hentGsakKodeTema(it),
                "dagerFrist" to it.dagerFrist
            )
        }

    private fun hentPrioriteter(prioriteter: List<GsakKodeTema.Prioritet>): List<Map<String, Any?>> =
        prioriteter.map {
            mapOf(
                *hentGsakKodeTema(it)
            )
        }

    private fun hentUnderkategorier(underkategorier: List<GsakKodeTema.Underkategori>): List<Map<String, Any?>> =
        underkategorier.map {
            mapOf(
                *hentGsakKodeTema(it),
                "erGyldig" to it.erGyldig()
            )
        }

    private fun hentGsakKodeTema(kodeTema: GsakKodeTema): Array<Pair<String, Any?>> =
        arrayOf(Pair("kode", kodeTema.kode), Pair("tekst", kodeTema.tekst))

    fun OpprettOppgaveRequestDTO.fromDTO(): OpprettOppgaveRequest = OpprettOppgaveRequest(
        fnr = fnr,
        behandlesAvApplikasjon = "FS22",
        beskrivelse = beskrivelse,
        temagruppe = "",
        tema = temaKode,
        oppgavetype = oppgaveTypeKode,
        behandlingstype = HENVENDELSESTYPE_KODE,
        prioritet = prioritetKode,
        underkategoriKode = underkategoriKode,
        opprettetavenhetsnummer = opprettetavenhetsnummer,
        oppgaveFrist = kalkulerFrist(temaKode, oppgaveTypeKode),
        valgtEnhetsId = valgtEnhetId.toString(),
        behandlingskjedeId = behandlingskjedeId,
        dagerFrist = dagerFrist,
        ansvarligEnhetId = ansvarligEnhetId,
        ansvarligIdent = ansvarligIdent

    )

    fun OpprettSkjermetOppgaveDTO.fromDTO(): OpprettSkjermetOppgaveRequest = OpprettSkjermetOppgaveRequest(
        fnr = fnr,
        behandlesAvApplikasjon = "FS22",
        beskrivelse = beskrivelse,
        temagruppe = "",
        tema = temaKode,
        oppgavetype = oppgaveTypeKode,
        behandlingstype = HENVENDELSESTYPE_KODE,
        prioritet = prioritetKode,
        underkategoriKode = underkategoriKode,
        opprettetavenhetsnummer = opprettetavenhetsnummer,
        oppgaveFrist = kalkulerFrist(temaKode, oppgaveTypeKode)
    )

    fun OpprettOppgaveResponse.toDTO(): OpprettOppgaveResponseDTO = OpprettOppgaveResponseDTO(
        id = id
    )
}

data class OpprettOppgaveRequestDTO(
    val fnr: String,
    val opprettetavenhetsnummer: String,
    val valgtEnhetId: Int,
    val behandlingskjedeId: String,
    val dagerFrist: Int,
    val ansvarligEnhetId: String,
    val ansvarligIdent: String?,
    val beskrivelse: String,
    val temaKode: String,
    val underkategoriKode: String?,
    val brukerid: String,
    val oppgaveTypeKode: String,
    val prioritetKode: String
)

data class OpprettSkjermetOppgaveDTO(
    val opprettetavenhetsnummer: String,
    val fnr: String,
    val beskrivelse: String,
    val temaKode: String,
    val underkategoriKode: String?,
    val brukerid: String,
    val oppgaveTypeKode: String,
    val prioritetKode: String
)

data class OpprettOppgaveResponseDTO(
    val id: String
)
