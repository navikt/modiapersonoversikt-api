package no.nav.modiapersonoversikt.rest.dialog.henvendelse

import no.nav.common.auth.subject.SubjectHandler
import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist
import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Fritekst
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Melding
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Meldingstype
import no.nav.modiapersonoversikt.legacy.api.exceptions.TraadAlleredeBesvart
import no.nav.modiapersonoversikt.legacy.api.service.HenvendelseUtsendingService
import no.nav.modiapersonoversikt.legacy.api.service.OppgaveBehandlingService
import no.nav.modiapersonoversikt.legacy.api.utils.RestUtils
import no.nav.modiapersonoversikt.legacy.api.utils.TemagruppeTemaMapping
import no.nav.modiapersonoversikt.legacy.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService
import no.nav.modiapersonoversikt.legacy.sporsmalogsvar.consumer.henvendelse.domain.Traad
import no.nav.modiapersonoversikt.rest.api.toDTO
import no.nav.modiapersonoversikt.rest.dialog.apis.*
import no.nav.modiapersonoversikt.service.sfhenvendelse.EksternBruker
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.servlet.http.HttpServletRequest

class HenvendelseDialog(
    private val henvendelseService: HenvendelseBehandlingService,
    private val henvendelseUtsendingService: HenvendelseUtsendingService,
    private val oppgaveBehandlingService: OppgaveBehandlingService,
    private val sfDialogController: SfHenvendelseService
) : DialogApi {
    val sfExperiment = Scientist.createExperiment<List<TraadDTO>>(
        Scientist.Config(
            name = "SF-Meldinger",
            experimentRate = 1.0,
            logAndCompareValues = false
        )
    )

    override fun hentMeldinger(request: HttpServletRequest, fnr: String, enhet: String?): List<TraadDTO> {
        val valgtEnhet = RestUtils.hentValgtEnhet(enhet, request)
        return sfExperiment.runWithExtraFields(
            control = {
                val value: List<TraadDTO> = henvendelseService
                    .hentMeldinger(fnr, valgtEnhet)
                    .traader
                    .toDTO()

                val sfRelevanteTrader = value.filter(::tradUtenVarselMelding)
                Scientist.WithFields(value, mapOf("control-length" to sfRelevanteTrader.size))
            },
            experiment = {
                val value = sfDialogController.hentHenvendelser(EksternBruker.Fnr(fnr), valgtEnhet)
                Scientist.WithFields(value, mapOf("experiment-length" to value.size))
            },
            dataFields = { control, experiment ->
                val sfRelevanteTrader = control.filter(::tradUtenVarselMelding).size
                val experimentSize = when (experiment) {
                    is List<*> -> experiment.size
                    else -> -1
                }
                mapOf("equal-length" to (sfRelevanteTrader == experimentSize))
            }
        )
    }

    override fun sendMelding(
        request: HttpServletRequest,
        fnr: String,
        referatRequest: SendReferatRequest
    ): ResponseEntity<Void> {
        val context = lagSendHenvendelseContext(fnr, referatRequest.enhet, request)
        henvendelseUtsendingService.sendHenvendelse(lagReferat(referatRequest, context), Optional.empty(), Optional.empty(), context.enhet)
        return ResponseEntity(HttpStatus.OK)
    }

    override fun sendSporsmal(
        request: HttpServletRequest,
        fnr: String,
        sporsmalsRequest: SendSporsmalRequest
    ): ResponseEntity<Void> {
        val context = lagSendHenvendelseContext(fnr, sporsmalsRequest.enhet, request)

        henvendelseUtsendingService.sendHenvendelse(lagSporsmal(sporsmalsRequest, sporsmalsRequest.sak.temaKode, context), Optional.empty(), Optional.of(sporsmalsRequest.sak), context.enhet)
        return ResponseEntity(HttpStatus.OK)
    }

    override fun sendInfomelding(
        request: HttpServletRequest,
        fnr: String,
        infomeldingRequest: InfomeldingRequest
    ): ResponseEntity<Void> {
        val context = lagSendHenvendelseContext(fnr, infomeldingRequest.enhet, request)

        henvendelseUtsendingService.sendHenvendelse(lagInfomelding(infomeldingRequest, infomeldingRequest.sak.temaKode, context), Optional.empty(), Optional.of(infomeldingRequest.sak), context.enhet)
        return ResponseEntity(HttpStatus.OK)
    }

    override fun startFortsettDialog(
        request: HttpServletRequest,
        fnr: String,
        ignorerConflict: Boolean?,
        opprettHenvendelseRequest: OpprettHenvendelseRequest
    ): FortsettDialogDTO {
        val traadId = opprettHenvendelseRequest.traadId
        val context = lagSendHenvendelseContext(fnr, opprettHenvendelseRequest.enhet, request)

        val traad = henvendelseService
            .hentMeldinger(fnr, context.enhet)
            .traader
            .find { it.traadId == traadId }
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Fant ingen tråd med id: $traadId")

        val oppgaveId: String? = finnOgTilordneOppgaveIdTilTrad(traad, context, ignorerConflict ?: false)

        val behandlingsId = henvendelseUtsendingService.opprettHenvendelse(
            Meldingstype.SVAR_SKRIFTLIG.name,
            context.fnr,
            traadId
        )

        return FortsettDialogDTO(behandlingsId, oppgaveId)
    }

    override fun sendFortsettDialog(
        request: HttpServletRequest,
        fnr: String,
        fortsettDialogRequest: FortsettDialogRequest
    ): ResponseEntity<Void> {
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

        return ResponseEntity(HttpStatus.OK)
    }

    override fun slaaSammenTraader(
        request: HttpServletRequest,
        fnr: String,
        slaaSammenRequest: SlaaSammenRequest
    ): Map<String, Any?> {
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

        return mapOf(
            "traader" to traader,
            "nyTraadId" to nyTraadId
        )
    }

    private fun finnOgTilordneOppgaveIdTilTrad(
        traad: Traad,
        context: RequestContext,
        ignorerConflict: Boolean
    ): String? {
        if (erUbesvartSporsmalFraBruker(traad)) {
            val sporsmal = traad.meldinger.find { it.id == it.traadId }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Fant ingen spørsmål i tråd med id: ${traad.traadId}")

            try {
                oppgaveBehandlingService.tilordneOppgaveIGsak(
                    sporsmal.oppgaveId,
                    Temagruppe.valueOf(sporsmal.temagruppe),
                    context.enhet,
                    ignorerConflict
                )
            } catch (e: OppgaveBehandlingService.AlleredeTildeltAnnenSaksbehandler) {
                throw ResponseStatusException(HttpStatus.CONFLICT, e.message)
            }
            return sporsmal.oppgaveId
        } else {
            return null
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
        .withTemagruppe(TemagruppeTemaMapping.hentTemagruppeForTema(sakstema))
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
        .withTemagruppe(TemagruppeTemaMapping.hentTemagruppeForTema(sakstema))
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

fun lagSendHenvendelseContext(fnr: String, enhet: String?, request: HttpServletRequest): RequestContext {
    val ident = SubjectHandler.getIdent().get()
    val enhet = RestUtils.hentValgtEnhet(enhet, request)

    require(enhet != null)
    return RequestContext(fnr, ident, enhet)
}

fun getKanal(type: Meldingstype): String {
    return when (type) {
        Meldingstype.SAMTALEREFERAT_OPPMOTE, Meldingstype.SVAR_OPPMOTE -> Kanal.OPPMOTE.name
        Meldingstype.SAMTALEREFERAT_TELEFON, Meldingstype.SVAR_TELEFON -> Kanal.TELEFON.name
        else -> Kanal.TEKST.name
    }
}

private fun tradUtenVarselMelding(traad: TraadDTO): Boolean {
    val meldingstyper = traad.meldinger.map { it.map["meldingstype"] }
    val harDokumentVarsel = meldingstyper.contains(Meldingstype.DOKUMENT_VARSEL.name)
    val harOppgaveVarsel = meldingstyper.contains(Meldingstype.OPPGAVE_VARSEL.name)
    return !harDokumentVarsel && !harOppgaveVarsel
}

enum class Kanal {
    TEKST,
    OPPMOTE,
    TELEFON
}

data class RequestContext(
    val fnr: String,
    val ident: String,
    val enhet: String
)
