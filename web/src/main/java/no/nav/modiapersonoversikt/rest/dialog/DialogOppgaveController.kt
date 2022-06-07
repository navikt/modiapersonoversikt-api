package no.nav.modiapersonoversikt.rest.dialog

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.CREATE
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person.Henvendelse
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave.OppgaveKodeverk
import no.nav.modiapersonoversikt.service.oppgavebehandling.OppgaveBehandlingService
import no.nav.modiapersonoversikt.service.oppgavebehandling.OpprettOppgaveRequest
import no.nav.modiapersonoversikt.service.oppgavebehandling.OpprettOppgaveResponse
import no.nav.modiapersonoversikt.service.oppgavebehandling.OpprettSkjermetOppgaveRequest
import no.nav.modiapersonoversikt.service.sfhenvendelse.fixKjedeId
import no.nav.modiapersonoversikt.utils.arbeidsdagerFraDatoJava
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

private const val HENVENDELSESTYPE_KODE: String = "DIALOG"

@RestController
@RequestMapping("/rest/dialogoppgave")
class DialogOppgaveController @Autowired constructor(
    private val oppgavebehandling: OppgaveBehandlingService,
    private val tilgangskontroll: Tilgangskontroll,
    private val kodeverkService: EnhetligKodeverk.Service
) {

    @PostMapping("/v2/opprett")
    fun opprettOppgave(@RequestBody request: OpprettOppgaveRequestDTO): OpprettOppgaveResponseDTO {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(request.fnr)))
            .check(Policies.henvendelseTilhorerBruker(Fnr(request.fnr), request.behandlingskjedeId))
            .get(Audit.describe(CREATE, Henvendelse.Oppgave.Opprett, AuditIdentifier.FNR to request.fnr, AuditIdentifier.BEHANDLING_ID to request.behandlingskjedeId)) {
                oppgavebehandling.opprettOppgave(request.fromDTO()).toDTO()
            }
    }

    @PostMapping("/v2/opprettskjermetoppgave")
    fun opprettSkjermetOppgave(
        @RequestBody request: OpprettSkjermetOppgaveDTO
    ): OpprettOppgaveResponseDTO {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(CREATE, Henvendelse.Oppgave.Opprett, AuditIdentifier.FNR to request.fnr)) {
                oppgavebehandling.opprettSkjermetOppgave(request.fromDTO()).toDTO()
            }
    }

    @GetMapping("/v2/tema")
    fun hentAlleTema(): List<OppgaveKodeverk.Tema> {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.skipAuditLog()) {
                kodeverkService.hentKodeverk(KodeverkConfig.OPPGAVE).hentAlleVerdier().toList()
            }
    }

    private fun kalkulerFrist(temaKode: String, oppgaveTypeKode: String): LocalDate {
        val dagerFrist = kodeverkService.hentKodeverk(KodeverkConfig.OPPGAVE).hentVerdiEllerNull(temaKode)
            ?.oppgavetyper
            ?.find { it.kode == oppgaveTypeKode }
            ?.dagerFrist
            ?: 2
        return arbeidsdagerFraDatoJava(dagerFrist, LocalDate.now())
    }

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
        behandlingskjedeId = behandlingskjedeId.fixKjedeId(),
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
