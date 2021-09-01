package no.nav.modiapersonoversikt.rest.dialog.salesforce

import no.nav.common.auth.subject.SubjectHandler
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.KjedeIdTilgangData
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.api.domain.Oppgave
import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.HenvendelseDTO
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.SamtalereferatRequestDTO
import no.nav.modiapersonoversikt.legacy.api.service.OppgaveBehandlingService
import no.nav.modiapersonoversikt.service.sfhenvendelse.EksternBruker
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/rest/sf-dialog")
class SfDialogController @Autowired constructor(
    private val tilgangskontroll: Tilgangskontroll,
    private val sfHenvendelseService: SfHenvendelseService,
    private val oppgaveBehandlingService: OppgaveBehandlingService
) {
    @GetMapping("/{fnr}")
    fun hentMeldinger(
        @PathVariable("fnr") fnr: String,
        @RequestParam(value = "enhet") enhet: String
    ): List<HenvendelseDTO> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                sfHenvendelseService.hentHenvendelser(EksternBruker.Fnr(fnr), enhet)
            }
    }

    data class OpprettOgSendMeldingRequest(
        val type: HenvendelseDTO.HenvendelseType,
        val temagruppe: String,
        val fritekst: String,
        val kanal: SamtalereferatRequestDTO.Kanal?
    )

    @PostMapping("/{fnr}")
    fun opprettNyDialogOgSendMelding(
        @PathVariable("fnr") fnr: String,
        @RequestParam("enhet") enhet: String,
        @RequestParam("oppgaveId", required = false) oppgaveId: String?,
        @RequestBody request: OpprettOgSendMeldingRequest
    ) {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(Audit.Action.CREATE, AuditResources.Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                val bruker = EksternBruker.Fnr(fnr)
                when (request.type) {
                    HenvendelseDTO.HenvendelseType.SAMTALEREFERAT ->
                        sfHenvendelseService.sendSamtalereferat(
                            bruker = bruker,
                            enhet = enhet,
                            temagruppe = request.temagruppe,
                            fritekst = request.fritekst,
                            kanal = requireNotNull(request.kanal) {
                                "Kanal må være satt ved sending av samtalereferat"
                            }
                        )
                    HenvendelseDTO.HenvendelseType.MELDINGSKJEDE -> {
                        sfHenvendelseService.opprettNyDialogOgSendMelding(
                            bruker = bruker,
                            enhet = enhet,
                            temagruppe = request.temagruppe,
                            fritekst = request.fritekst
                        )
                    }
                }
            }
    }

    data class SendPaEksisterendeRequest(val fritekst: String)

    @PostMapping("/{fnr}/{kjedeId}")
    fun sendMeldingPaEksisterendeDialog(
        @PathVariable("fnr") fnr: String,
        @PathVariable("kjedeId") kjedeId: String,
        @RequestParam("enhet") enhet: String,
        @RequestParam("oppgaveId", required = false) oppgaveId: String?,
        @RequestBody request: SendPaEksisterendeRequest
    ) {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(Audit.Action.CREATE, AuditResources.Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                val bruker = EksternBruker.Fnr(fnr)
                val henvendelse = sfHenvendelseService.hentHenvendelse(kjedeId)
                val henvendelseTilhorerBruker = sfHenvendelseService.sjekkEierskap(bruker, henvendelse)
                if (!henvendelseTilhorerBruker) {
                    throw ResponseStatusException(HttpStatus.FORBIDDEN, "Henvendelse $kjedeId tilhørte ikke bruker")
                }

                if (oppgaveId != null) {
                    val oppgave: Oppgave? = oppgaveBehandlingService.hentOppgave(oppgaveId)
                    val oppgaveTilknyttetKjede: Boolean = oppgave?.let { it.henvendelseId == kjedeId } ?: false
                    if (!oppgaveTilknyttetKjede) {
                        throw ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Feil oppgaveId fra client. Forventet '$kjedeId', men fant '${oppgave?.henvendelseId}'"
                        )
                    } else if (oppgaveBehandlingService.oppgaveErFerdigstilt(oppgaveId)) {
                        throw ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Feil oppgaveId fra client. Oppgaven er allerede ferdigstilt"
                        )
                    }
                }

                sfHenvendelseService.sendMeldingPaEksisterendeDialog(
                    kjedeId = kjedeId,
                    bruker = bruker,
                    enhet = enhet,
                    fritekst = request.fritekst
                )

                if (oppgaveId != null) {
                    oppgaveBehandlingService.ferdigstillOppgaveIGsak(
                        oppgaveId,
                        Temagruppe.valueOf(henvendelse.gjeldendeTemagruppe),
                        enhet
                    )
                }
            }
    }

    data class JournalforHenvendelseRequest(val saksId: String?, val saksTema: String)

    @PostMapping("/{fnr}/{kjedeId}/journalfor")
    fun journalforHenvendelse(
        @PathVariable("fnr") fnr: String,
        @PathVariable("kjedeId") kjedeId: String,
        @RequestParam("enhet") enhet: String,
        @RequestBody request: JournalforHenvendelseRequest
    ) {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to fnr,
            AuditIdentifier.TRAAD_ID to kjedeId,
            AuditIdentifier.SAK_ID to (request.saksId ?: request.saksTema)
        )
        tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .check(Policies.sfDialogTilhorerBruker.with(KjedeIdTilgangData(fnr, kjedeId)))
            .get(Audit.describe(Audit.Action.UPDATE, AuditResources.Person.Henvendelse.Journalfor, *auditIdentifier)) {
                sfHenvendelseService.journalforHenvendelse(
                    enhet = enhet,
                    kjedeId = kjedeId,
                    saksId = request.saksId,
                    saksTema = request.saksTema
                )
            }
    }

    @PostMapping("/{fnr}/{kjedeId}/merkSomFeilsendt")
    fun markerSomFeilsendt(
        @PathVariable("fnr") fnr: String,
        @PathVariable("kjedeId") kjedeId: String
    ) {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to fnr,
            AuditIdentifier.TRAAD_ID to kjedeId
        )
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .check(Policies.sfDialogTilhorerBruker.with(KjedeIdTilgangData(fnr, kjedeId)))
            .get(Audit.describe(Audit.Action.UPDATE, AuditResources.Person.Henvendelse.Merk.Feilsendt, *auditIdentifier)) {
                sfHenvendelseService.merkSomFeilsendt(kjedeId)
            }
    }

    @PostMapping("/{fnr}/{kjedeId}/merkSomKontorsperret")
    fun merkSomKontorsperret(
        @PathVariable("fnr") fnr: String,
        @PathVariable("kjedeId") kjedeId: String,
        @RequestParam(value = "enhet") enhet: String
    ) {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to fnr,
            AuditIdentifier.TRAAD_ID to kjedeId
        )
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .check(Policies.sfDialogTilhorerBruker.with(KjedeIdTilgangData(fnr, kjedeId)))
            .get(Audit.describe(Audit.Action.UPDATE, AuditResources.Person.Henvendelse.Merk.Kontorsperre, *auditIdentifier)) {
                sfHenvendelseService.merkSomKontorsperret(kjedeId, enhet)
            }
    }

    @PostMapping("/{fnr}/{kjedeId}/merkForHastekassering")
    fun merkForHastekassering(
        @PathVariable("fnr") fnr: String,
        @PathVariable("kjedeId") kjedeId: String
    ) {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to fnr,
            AuditIdentifier.TRAAD_ID to kjedeId
        )
        return tilgangskontroll
            .check(Policies.kanHastekassere)
            .check(Policies.tilgangTilBruker.with(fnr))
            .check(Policies.sfDialogTilhorerBruker.with(KjedeIdTilgangData(fnr, kjedeId)))
            .get(Audit.describe(Audit.Action.UPDATE, AuditResources.Person.Henvendelse.Merk.Slett, *auditIdentifier)) {
                sfHenvendelseService.merkForHastekassering(kjedeId)
            }
    }

    @GetMapping("/kanMerkForHastekassering")
    fun kanMerkeForHasteKassering(): Boolean {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.skipAuditLog()) {
                val godkjenteSaksbehandlere = tilgangskontroll.context().hentSaksbehandlereMedTilgangTilHastekassering()
                val saksbehandlerId = SubjectHandler.getIdent().map(String::toUpperCase).get()
                godkjenteSaksbehandlere.contains(saksbehandlerId)
            }
    }
}