package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog

import no.nav.common.auth.subject.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype
import no.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.TraadAlleredeBesvart
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.TemagruppeTemaMapping.hentTemagruppeForTema
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.api.DTO
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.api.toDTO
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.*
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Traad
import no.nav.sbl.dialogarena.naudit.AuditResources.Person
import no.nav.sbl.dialogarena.rsbac.DecisionEnums
import no.nav.sbl.dialogarena.rsbac.Policy
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

data class BehandlingsId(val behandlingsId: String)
data class TraadDTO(val traadId: String, val meldinger: List<MeldingDTO>) : DTO
class MeldingDTO(val map: Map<String, Any?>) : HashMap<String, Any?>(map), DTO
class FortsettDialogDTO(val behandlingsId: String, val oppgaveId: String?) : DTO

@Path("/dialog/{fnr}")
@Produces("application/json")
class DialogController @Autowired constructor(
        private val tilgangskontroll: Tilgangskontroll,
        private val henvendelseService: HenvendelseBehandlingService,
        private val henvendelseUtsendingService: HenvendelseUtsendingService,
        private val sakerService: SakerService,
        private val oppgaveBehandlingService: OppgaveBehandlingService,
        private val unleashService: UnleashService
) {
    @GET
    @Path("/meldinger")
    fun hentMeldinger(
            @Context request: HttpServletRequest,
            @PathParam("fnr") fnr: String
    ): List<TraadDTO> {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get(Audit.describe(READ, Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                    val valgtEnhet = RestUtils.hentValgtEnhet(request)
                    henvendelseService
                            .hentMeldinger(fnr, valgtEnhet)
                            .traader
                            .toDTO()

                }
    }

    @POST
    @Path("/sendreferat")
    fun sendMelding(
            @Context request: HttpServletRequest,
            @PathParam("fnr") fnr: String,
            referatRequest: SendReferatRequest
    ): Response {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get(Audit.describe(CREATE, Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                    val context = lagSendHenvendelseContext(fnr, request)
                    val behandlingsId = henvendelseUtsendingService.sendHenvendelse(lagReferat(referatRequest, context), Optional.empty(), Optional.empty(), context.enhet)
                    Response.ok(BehandlingsId(behandlingsId)).build()
                }
    }

    @POST
    @Path("/sendsporsmal")
    fun sendSporsmal(
            @Context request: HttpServletRequest,
            @PathParam("fnr") fnr: String,
            sporsmalsRequest: SendSporsmalRequest
    ): Response {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get(Audit.describe(CREATE, Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                    val context = lagSendHenvendelseContext(fnr, request)

                    henvendelseUtsendingService.sendHenvendelse(lagSporsmal(sporsmalsRequest, sporsmalsRequest.sak.temaKode, context), Optional.empty(), Optional.of(sporsmalsRequest.sak), context.enhet)
                    Response.ok().build()
                }
    }

    @POST
    @Path("/sendinfomelding")
    fun sendInfomelding(
            @Context request: HttpServletRequest,
            @PathParam("fnr") fnr: String,
            infomeldingRequest: InfomeldingRequest
    ): Response {
        return tilgangskontroll
                .check(Policies.featureToggleEnabled.with(Feature.INFOMELDING.propertyKey))
                .check(Policies.tilgangTilBruker.with(fnr))
                .get(Audit.describe(CREATE, Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                    val context = lagSendHenvendelseContext(fnr, request)

                    henvendelseUtsendingService.sendHenvendelse(lagInfomelding(infomeldingRequest, infomeldingRequest.sak.temaKode, context), Optional.empty(), Optional.of(infomeldingRequest.sak), context.enhet)
                    Response.ok().build()
                }
    }

    @POST
    @Path("/fortsett/opprett")
    fun startFortsettDialog(
            @Context request: HttpServletRequest,
            @PathParam("fnr") fnr: String,
            opprettHenvendelseRequest: OpprettHenvendelseRequest
    ): FortsettDialogDTO {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get(Audit.describe(CREATE, Person.Henvendelse.Opprettet, AuditIdentifier.FNR to fnr)) {
                    val traadId = opprettHenvendelseRequest.traadId
                    val context = lagSendHenvendelseContext(fnr, request)
                    val traad = henvendelseService
                            .hentMeldinger(fnr, context.enhet)
                            .traader
                            .find { it.traadId == traadId }
                            ?: throw WebApplicationException("Fant ingen tråd med id: $traadId", 400)

                    val oppgaveId: String? = if (erUbesvartSporsmalFraBruker(traad)) {
                        val sporsmal = traad.meldinger.find { it.id == it.traadId }
                                ?: throw WebApplicationException("Fant ingen spørsmål i tråd med id: $traadId", 400)
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

    @POST
    @Path("/fortsett/ferdigstill")
    fun sendFortsettDialog(
            @Context request: HttpServletRequest,
            @PathParam("fnr") fnr: String,
            fortsettDialogRequest: FortsettDialogRequest
    ): Response {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get(Audit.describe(UPDATE, Person.Henvendelse.Ferdigstill, AuditIdentifier.FNR to fnr)) {
                    val context = lagSendHenvendelseContext(fnr, request)
                    val traad = henvendelseService
                            .hentMeldinger(fnr, context.enhet)
                            .traader
                            .find { it.traadId == fortsettDialogRequest.traadId }
                            ?: throw WebApplicationException("Fant ingen tråd med id: ${fortsettDialogRequest.traadId}", 400)

                    henvendelseUtsendingService.ferdigstillHenvendelse(
                            lagFortsettDialog(fortsettDialogRequest, context, traad),
                            Optional.ofNullable(fortsettDialogRequest.oppgaveId),
                            Optional.ofNullable(fortsettDialogRequest.sak),
                            fortsettDialogRequest.behandlingsId,
                            context.enhet
                    )

                    Response.ok().build()
                }
    }

    @POST
    @Path("slaasammen")
    fun slaaSammenTraader(
            @Context request: HttpServletRequest,
            @PathParam("fnr") fnr: String,
            slaaSammenRequest: SlaaSammenRequest
    ): Map<String, Any?> = tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(UPDATE, Person.Henvendelse.SlaSammen, AuditIdentifier.FNR to fnr)) {
                if (slaaSammenRequest.traader.groupingBy { it -> it.traadId }.eachCount().size < 2) {
                    throw BadRequestException("Du kan ikke slå sammen mindre enn 2 trådeer")
                }

                if (sjekkOmNoenOppgaverErFerdigstilt(slaaSammenRequest)) {
                    throw BadRequestException("En eller fler av oppgavene er allerede ferdigstilt")
                }
                val nyTraadId = try {
                    henvendelseUtsendingService.slaaSammenTraader(slaaSammenRequest.traader.map { it.traadId })
                } catch (e: TraadAlleredeBesvart) {
                    throw BadRequestException("En eller fler av trådene er allerede besvart")
                }

                val valgtEnhet = RestUtils.hentValgtEnhet(request)
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

private fun lagInfomelding(infomeldingRequest: InfomeldingRequest, sakstema: String, requestContext: RequestContext) : Melding {
    val type = Meldingstype.INFOMELDING_MODIA_UTGAAENDE
    return Melding().withFnr(requestContext.fnr)
            .withNavIdent(requestContext.ident)
            .withEksternAktor(requestContext.ident)
            .withKanal(getKanal(type))
            .withType(type)
            .withFritekst(Fritekst(infomeldingRequest.fritekst))
            .withTilknyttetEnhet(requestContext.enhet)
            .withErTilknyttetAnsatt(infomeldingRequest.erOppgaveTilknyttetAnsatt)
            .withTemagruppe(hentTemagruppeForTema(sakstema))
}

private fun erTraadTilknyttetAnsatt(traad: Traad): Boolean =
        if (traad.meldinger.any { it.meldingstype == Meldingstype.SPORSMAL_MODIA_UTGAAENDE }) {
            traad.meldinger.sortedBy { it.visningsDato }.last().erTilknyttetAnsatt
        } else {
            true
        }

private fun lagFortsettDialog(request: FortsettDialogRequest, requestContext: RequestContext, traad: Traad): Melding {
    val eldsteMelding = traad.meldinger[0]
    val erOppgaveTilknyttetAnsatt = if (request.meldingstype == Meldingstype.SPORSMAL_MODIA_UTGAAENDE) request.erOppgaveTilknyttetAnsatt else erTraadTilknyttetAnsatt(traad)
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
        val traadId: String
)

data class SendReferatRequest(
        val fritekst: String,
        val temagruppe: String,
        val meldingstype: Meldingstype
)

data class SendSporsmalRequest(
        val fritekst: String,
        val sak: Sak,
        val erOppgaveTilknyttetAnsatt: Boolean
)

data class InfomeldingRequest(
        val fritekst: String,
        val sak: Sak,
        val erOppgaveTilknyttetAnsatt: Boolean
)

data class FortsettDialogRequest(
        val traadId: String,
        val behandlingsId: String,
        val fritekst: String,
        val sak: Sak?,
        val erOppgaveTilknyttetAnsatt: Boolean,
        val meldingstype: Meldingstype,
        val oppgaveId: String?
)

fun lagSendHenvendelseContext(fnr: String, request: HttpServletRequest): RequestContext {
    val ident = SubjectHandler.getIdent().get()
    val enhet = RestUtils.hentValgtEnhet(request)

    require(enhet != null)
    return RequestContext(fnr, ident, enhet)
}

data class RequestContext(
        val fnr: String,
        val ident: String,
        val enhet: String
)

data class SlaaSammenRequest(
        val traader: List<SlaaSammenTraad>,
        val temagruppe: Temagruppe
)

data class SlaaSammenTraad(
        val oppgaveId: String,
        val traadId: String
)
