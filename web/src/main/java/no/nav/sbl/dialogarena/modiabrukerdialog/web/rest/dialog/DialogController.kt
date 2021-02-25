package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog

import no.nav.common.auth.subject.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.saker.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.TraadAlleredeBesvart
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saker.SakerService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.TemagruppeTemaMapping.hentTemagruppeForTema
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.api.DTO
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.api.toDTO
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.*
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import no.nav.sbl.dialogarena.naudit.AuditResources.Person
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Traad
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.servlet.http.HttpServletRequest

data class BehandlingsId(val behandlingsId: String)
data class TraadDTO(val traadId: String, val meldinger: List<MeldingDTO>) : DTO
class MeldingDTO(val map: Map<String, Any?>) : HashMap<String, Any?>(map), DTO
class FortsettDialogDTO(val behandlingsId: String, val oppgaveId: String?) : DTO

@RestController
@RequestMapping("/rest/dialog/{fnr}")
class DialogController @Autowired constructor(
    private val tilgangskontroll: Tilgangskontroll,
    private val henvendelseService: HenvendelseBehandlingService,
    private val henvendelseUtsendingService: HenvendelseUtsendingService,
    private val sakerService: SakerService,
    private val oppgaveBehandlingService: OppgaveBehandlingService
) {
    @GetMapping("/meldinger")
    fun hentMeldinger(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestParam(value = "enhet", required = false) enhet: String?
    ): List<TraadDTO> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(READ, Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                val valgtEnhet = RestUtils.hentValgtEnhet(enhet, request)
                henvendelseService
                    .hentMeldinger(fnr, valgtEnhet)
                    .traader
                    .toDTO()
            }
    }

    @PostMapping("/sendreferat")
    fun sendMelding(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody referatRequest: SendReferatRequest
    ): ResponseEntity<BehandlingsId> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(CREATE, Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                val context = lagSendHenvendelseContext(fnr, referatRequest.enhet, request)
                val behandlingsId = henvendelseUtsendingService.sendHenvendelse(lagReferat(referatRequest, context), Optional.empty(), Optional.empty(), context.enhet)
                ResponseEntity(BehandlingsId(behandlingsId), HttpStatus.OK)
            }
    }

    @PostMapping("/sendsporsmal")
    fun sendSporsmal(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody sporsmalsRequest: SendSporsmalRequest
    ): ResponseEntity<Void> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(CREATE, Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                val context = lagSendHenvendelseContext(fnr, sporsmalsRequest.enhet, request)

                henvendelseUtsendingService.sendHenvendelse(lagSporsmal(sporsmalsRequest, sporsmalsRequest.sak.temaKode, context), Optional.empty(), Optional.of(sporsmalsRequest.sak), context.enhet)
                ResponseEntity(HttpStatus.OK)
            }
    }

    @PostMapping("/sendinfomelding")
    fun sendInfomelding(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody infomeldingRequest: InfomeldingRequest
    ): ResponseEntity<Void> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(CREATE, Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                val context = lagSendHenvendelseContext(fnr, infomeldingRequest.enhet, request)

                henvendelseUtsendingService.sendHenvendelse(lagInfomelding(infomeldingRequest, infomeldingRequest.sak.temaKode, context), Optional.empty(), Optional.of(infomeldingRequest.sak), context.enhet)
                ResponseEntity(HttpStatus.OK)
            }
    }

    @PostMapping("/fortsett/opprett")
    fun startFortsettDialog(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody opprettHenvendelseRequest: OpprettHenvendelseRequest
    ): FortsettDialogDTO {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to fnr,
            AuditIdentifier.TRAAD_ID to opprettHenvendelseRequest.traadId
        )
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(CREATE, Person.Henvendelse.Opprettet, *auditIdentifier)) {
                val traadId = opprettHenvendelseRequest.traadId
                val context = lagSendHenvendelseContext(fnr, opprettHenvendelseRequest.enhet, request)

                val traad = henvendelseService
                    .hentMeldinger(fnr, context.enhet)
                    .traader
                    .find { it.traadId == traadId }
                    ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Fant ingen tråd med id: $traadId")

                val oppgaveId: String? = if (erUbesvartSporsmalFraBruker(traad)) {
                    val sporsmal = traad.meldinger.find { it.id == it.traadId }
                        ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Fant ingen spørsmål i tråd med id: $traadId")
                    oppgaveBehandlingService.tilordneOppgaveIGsak(
                        sporsmal.oppgaveId,
                        Temagruppe.valueOf(sporsmal.temagruppe),
                        context.enhet
                    )

                    sporsmal.oppgaveId
                } else null

                val behandlingsId = henvendelseUtsendingService.opprettHenvendelse(
                    Meldingstype.SVAR_SKRIFTLIG.name,
                    context.fnr,
                    traadId
                )

                FortsettDialogDTO(behandlingsId, oppgaveId)
            }
    }

    @PostMapping("/fortsett/ferdigstill")
    fun sendFortsettDialog(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody fortsettDialogRequest: FortsettDialogRequest
    ): ResponseEntity<Void> {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to fnr,
            AuditIdentifier.BEHANDLING_ID to fortsettDialogRequest.behandlingsId,
            AuditIdentifier.OPPGAVE_ID to fortsettDialogRequest.oppgaveId
        )
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(UPDATE, Person.Henvendelse.Ferdigstill, *auditIdentifier)) {
                val context = lagSendHenvendelseContext(fnr, fortsettDialogRequest.enhet, request)
                val oppgave = fortsettDialogRequest.oppgaveId?.let(oppgaveBehandlingService::hentOppgave)
                val traad = henvendelseService
                    .hentMeldinger(fnr, context.enhet)
                    .traader
                    .find { it.traadId == fortsettDialogRequest.traadId }
                    ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Fant ingen tråd med id: ${fortsettDialogRequest.traadId}")

                if (!traad.besvaringKanFerdigstilleOppgave(oppgave)) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Feil oppgaveId fra client. Forventet: ${traad.eldsteMelding.oppgaveId} men oppdaget: ${fortsettDialogRequest.oppgaveId}")
                }

                henvendelseUtsendingService.ferdigstillHenvendelse(
                    lagFortsettDialog(fortsettDialogRequest, context, traad),
                    Optional.ofNullable(fortsettDialogRequest.oppgaveId),
                    Optional.ofNullable(fortsettDialogRequest.sak),
                    fortsettDialogRequest.behandlingsId,
                    context.enhet
                )

                ResponseEntity(HttpStatus.OK)
            }
    }

    @PostMapping("slaasammen")
    fun slaaSammenTraader(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody slaaSammenRequest: SlaaSammenRequest
    ): Map<String, Any?> {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to fnr,
            AuditIdentifier.TRAAD_ID to slaaSammenRequest.traader.joinToString(" ") {
                "${it.traadId}:${it.oppgaveId}"
            }
        )
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(UPDATE, Person.Henvendelse.SlaSammen, *auditIdentifier)) {
                if (slaaSammenRequest.traader.groupingBy { it.traadId }.eachCount().size < 2) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Du kan ikke slå sammen mindre enn 2 trådeer")
                }

                if (sjekkOmNoenOppgaverErFerdigstilt(slaaSammenRequest)) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "En eller fler av oppgavene er allerede ferdigstilt")
                }
                val nyTraadId = try {
                    henvendelseUtsendingService.slaaSammenTraader(slaaSammenRequest.traader.map { it.traadId })
                } catch (e: TraadAlleredeBesvart) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "En eller fler av trådene er allerede besvart")
                }

                val valgtEnhet = RestUtils.hentValgtEnhet(slaaSammenRequest.enhet, request)
                ferdigstillAlleUnntattEnOppgave(slaaSammenRequest, nyTraadId, valgtEnhet)

                val traader: List<TraadDTO> = henvendelseService
                    .hentMeldinger(fnr, valgtEnhet)
                    .traader
                    .toDTO()

                mapOf(
                    "traader" to traader,
                    "nyTraadId" to nyTraadId
                )
            }
    }

    private fun ferdigstillAlleUnntattEnOppgave(request: SlaaSammenRequest, nyTraadId: String, enhet: String) {
        request.traader.filter { it.traadId != nyTraadId }.forEach {
            oppgaveBehandlingService.ferdigstillOppgaveIGsak(it.oppgaveId, request.temagruppe, enhet)
        }
    }

    private fun sjekkOmNoenOppgaverErFerdigstilt(request: SlaaSammenRequest): Boolean {
        request.traader.map { it.oppgaveId }.forEach {
            if (oppgaveBehandlingService.oppgaveErFerdigstilt(it)) {
                return true
            }
        }
        return false
    }
}

private fun erUbesvartSporsmalFraBruker(traad: Traad): Boolean {
    return traad
        .meldinger
        .filter { !it.erDelvisSvar() }
        .filter { !(it.meldingstype == Meldingstype.SPORSMAL_SKRIFTLIG || it.meldingstype == Meldingstype.SPORSMAL_SKRIFTLIG_DIREKTE) }
        .isEmpty()
}

private fun lagReferat(referatRequest: SendReferatRequest, requestContext: RequestContext): Melding {
    return Melding().withFnr(requestContext.fnr)
        .withNavIdent(requestContext.ident)
        .withEksternAktor(requestContext.ident)
        .withKanal(getKanal(referatRequest.meldingstype))
        .withType(referatRequest.meldingstype)
        .withFritekst(Fritekst(referatRequest.fritekst))
        .withTilknyttetEnhet(requestContext.enhet)
        .withErTilknyttetAnsatt(true)
        .withTemagruppe(referatRequest.temagruppe)
}

private fun lagSporsmal(sporsmalRequest: SendSporsmalRequest, sakstema: String, requestContext: RequestContext): Melding {
    val type = Meldingstype.SPORSMAL_MODIA_UTGAAENDE
    return Melding().withFnr(requestContext.fnr)
        .withNavIdent(requestContext.ident)
        .withEksternAktor(requestContext.ident)
        .withKanal(getKanal(type))
        .withType(type)
        .withFritekst(Fritekst(sporsmalRequest.fritekst))
        .withTilknyttetEnhet(requestContext.enhet)
        .withErTilknyttetAnsatt(sporsmalRequest.erOppgaveTilknyttetAnsatt)
        .withTemagruppe(hentTemagruppeForTema(sakstema))
}

private fun lagInfomelding(infomeldingRequest: InfomeldingRequest, sakstema: String, requestContext: RequestContext): Melding {
    val type = Meldingstype.INFOMELDING_MODIA_UTGAAENDE
    return Melding().withFnr(requestContext.fnr)
        .withNavIdent(requestContext.ident)
        .withEksternAktor(requestContext.ident)
        .withKanal(getKanal(type))
        .withType(type)
        .withFritekst(Fritekst(infomeldingRequest.fritekst))
        .withTilknyttetEnhet(requestContext.enhet)
        .withErTilknyttetAnsatt(true)
        .withTemagruppe(hentTemagruppeForTema(sakstema))
}

private fun erTraadTilknyttetAnsatt(traad: Traad): Boolean =
    if (traad.meldinger.any { it.meldingstype == Meldingstype.SPORSMAL_MODIA_UTGAAENDE }) {
        traad.meldinger.sortedBy { it.visningsDato }.last().erTilknyttetAnsatt
    } else {
        true
    }

private fun lagFortsettDialog(request: FortsettDialogRequest, requestContext: RequestContext, traad: Traad): Melding {
    val eldsteMelding = traad.eldsteMelding
    val erOppgaveTilknyttetAnsatt =
        if (request.meldingstype == Meldingstype.SPORSMAL_MODIA_UTGAAENDE) {
            request.erOppgaveTilknyttetAnsatt
        } else {
            erTraadTilknyttetAnsatt(traad)
        }

    return Melding()
        .withFnr(requestContext.fnr)
        .withNavIdent(requestContext.ident)
        .withEksternAktor(requestContext.ident)
        .withKanal(getKanal(request.meldingstype))
        .withType(request.meldingstype)
        .withFritekst(Fritekst(request.fritekst))
        .withTilknyttetEnhet(requestContext.enhet)
        .withErTilknyttetAnsatt(erOppgaveTilknyttetAnsatt)
        .withTraadId(request.traadId)
        .withKontorsperretEnhet(eldsteMelding.kontorsperretEnhet)
        .withTemagruppe(eldsteMelding.temagruppe)
        .withBrukersEnhet(eldsteMelding.brukersEnhet)
}

enum class Kanal {
    TEKST,
    OPPMOTE,
    TELEFON
}

fun getKanal(type: Meldingstype): String {
    return when (type) {
        Meldingstype.SAMTALEREFERAT_OPPMOTE, Meldingstype.SVAR_OPPMOTE -> Kanal.OPPMOTE.name
        Meldingstype.SAMTALEREFERAT_TELEFON, Meldingstype.SVAR_TELEFON -> Kanal.TELEFON.name
        else -> Kanal.TEKST.name
    }
}

data class OpprettHenvendelseRequest(
    val enhet: String?,
    val traadId: String
)

data class SendReferatRequest(
    val enhet: String?,
    val fritekst: String,
    val temagruppe: String,
    val meldingstype: Meldingstype
)

data class SendSporsmalRequest(
    val enhet: String?,
    val fritekst: String,
    val sak: Sak,
    val erOppgaveTilknyttetAnsatt: Boolean
)

data class InfomeldingRequest(
    val enhet: String?,
    val fritekst: String,
    val sak: Sak
)

data class FortsettDialogRequest(
    val enhet: String?,
    val traadId: String,
    val behandlingsId: String,
    val fritekst: String,
    val sak: Sak?,
    val erOppgaveTilknyttetAnsatt: Boolean,
    val meldingstype: Meldingstype,
    val oppgaveId: String?
)

fun lagSendHenvendelseContext(fnr: String, enhet: String?, request: HttpServletRequest): RequestContext {
    val ident = SubjectHandler.getIdent().get()
    val enhet = RestUtils.hentValgtEnhet(enhet, request)

    require(enhet != null)
    return RequestContext(fnr, ident, enhet)
}

data class RequestContext(
    val fnr: String,
    val ident: String,
    val enhet: String
)

data class SlaaSammenRequest(
    val enhet: String?,
    val traader: List<SlaaSammenTraad>,
    val temagruppe: Temagruppe
)

data class SlaaSammenTraad(
    val oppgaveId: String,
    val traadId: String
)
