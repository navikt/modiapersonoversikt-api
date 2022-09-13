package no.nav.modiapersonoversikt.rest.dialog.salesforce

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.commondomain.Temagruppe
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.HenvendelseDTO
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.SamtalereferatRequestDTO
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.oppgavebehandling.Oppgave
import no.nav.modiapersonoversikt.service.oppgavebehandling.OppgaveBehandlingService
import no.nav.modiapersonoversikt.service.sfhenvendelse.EksternBruker
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

/**
 * En forenklet api-modell som kan brukes av frontend-koden etterhver.
 * Per i dag vil frontend bruke de gamle api-ene implementert av `SfLegacyXXXXXX` filene
 *
 * Disabled for nå
 */
// @RestController
// @RequestMapping("/rest/sf-dialog")
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
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                sfHenvendelseService.hentHenvendelser(EksternBruker.Fnr(fnr), enhet)
            }
    }

    data class OpprettOgSendMeldingRequest(
        val type: HenvendelseDTO.HenvendelseType,
        val temagruppe: String,
        val tilknyttetAnsatt: Boolean,
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
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(Audit.Action.CREATE, AuditResources.Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                val bruker = EksternBruker.Fnr(fnr)
                when (request.type) {
                    HenvendelseDTO.HenvendelseType.SAMTALEREFERAT ->
                        sfHenvendelseService.sendSamtalereferat(
                            kjedeId = null,
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
                            tilknyttetAnsatt = request.tilknyttetAnsatt,
                            fritekst = request.fritekst
                        )
                    }
                    HenvendelseDTO.HenvendelseType.CHAT -> {
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Kan ikke opprette chat-melding fra modia")
                    }
                }
                Unit
            }
    }

    data class SendPaEksisterendeRequest(val tilknyttetAnsatt: Boolean, val fritekst: String)

    @PostMapping("/{fnr}/{kjedeId}")
    fun sendMeldingPaEksisterendeDialog(
        @PathVariable("fnr") fnr: String,
        @PathVariable("kjedeId") kjedeId: String,
        @RequestParam("enhet") enhet: String,
        @RequestParam("oppgaveId", required = false) oppgaveId: String?,
        @RequestBody request: SendPaEksisterendeRequest
    ) {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
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
                    tilknyttetAnsatt = request.tilknyttetAnsatt,
                    fritekst = request.fritekst
                )

                if (oppgaveId != null) {
                    oppgaveBehandlingService.ferdigstillOppgaveIGsak(
                        oppgaveId,
                        Temagruppe.valueOf(henvendelse.gjeldendeTemagruppe!!), // TODO må fikses av SF-api. Temagruppe kan ikke være null
                        enhet
                    )
                }
            }
    }

    data class JournalforHenvendelseRequest(
        val saksId: String,
        val fagsaksystem: String,
        val saksTema: String
    )

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
            AuditIdentifier.SAK_ID to request.saksId,
            AuditIdentifier.SAK_TEMA to request.saksTema
        )
        tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .check(Policies.henvendelseTilhorerBruker(Fnr(fnr), kjedeId))
            .get(Audit.describe(Audit.Action.UPDATE, AuditResources.Person.Henvendelse.Journalfor, *auditIdentifier)) {
                // NB Denne controlleren er ikke ibruk per idag. Men dette må tittes nærmere på før en evt overgang.
                sfHenvendelseService.journalforHenvendelse(
                    enhet = enhet,
                    kjedeId = kjedeId,
                    saksId = request.saksId,
                    fagsakSystem = request.fagsaksystem,
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
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .check(Policies.henvendelseTilhorerBruker(Fnr(fnr), kjedeId))
            .get(Audit.describe(Audit.Action.UPDATE, AuditResources.Person.Henvendelse.Merk.Feilsendt, *auditIdentifier)) {
                sfHenvendelseService.merkSomFeilsendt(kjedeId)
            }
    }

    @PostMapping("/{fnr}/{kjedeId}/sendTilSladding")
    fun sendTilSladding(
        @PathVariable("fnr") fnr: String,
        @PathVariable("kjedeId") kjedeId: String,
        @RequestParam(value = "enhet") enhet: String
    ) {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to fnr,
            AuditIdentifier.TRAAD_ID to kjedeId
        )
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .check(Policies.henvendelseTilhorerBruker(Fnr(fnr), kjedeId))
            .get(Audit.describe(Audit.Action.UPDATE, AuditResources.Person.Henvendelse.Merk.Sladding, *auditIdentifier)) {
                sfHenvendelseService.sendTilSladding(kjedeId)
            }
    }
}
