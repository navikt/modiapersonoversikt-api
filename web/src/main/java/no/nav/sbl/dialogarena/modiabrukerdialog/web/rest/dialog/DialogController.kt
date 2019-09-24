package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog

import no.nav.brukerdialog.security.context.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.TemagruppeTemaMapping.hentTemagruppeForTema
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.api.DTO
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.api.toDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.tilgangTilBruker
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Traad
import java.util.*
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

data class TraadDTO(val traadId: String, val meldinger: List<MeldingDTO>) : DTO
class MeldingDTO(val map: Map<String, Any?>) : HashMap<String, Any?>(map), DTO
class FortsettDialogDTO(val behandlingsId: String, val oppgaveId: String?) : DTO

@Path("/dialog/{fnr}")
@Produces("application/json")
class DialogController @Inject constructor(
        private val tilgangskontroll: Tilgangskontroll,
        private val henvendelseService: HenvendelseBehandlingService,
        private val henvendelseUtsendingService: HenvendelseUtsendingService,
        private val sakerService: SakerService,
        private val oppgaveBehandlingService: OppgaveBehandlingService
) {
    @GET
    @Path("/meldinger")
    fun hentMeldinger(
            @Context request: HttpServletRequest,
            @PathParam("fnr") fnr: String
    ): List<TraadDTO> {
        return tilgangskontroll
                .tilgangTilBruker(fnr)
                .get {
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
                .tilgangTilBruker(fnr)
                .get {
                    val context = lagSendHenvendelseContext(fnr, request)
                    henvendelseUtsendingService.sendHenvendelse(lagReferat(referatRequest, context), Optional.empty(), Optional.empty(), context.enhet)
                    Response.ok().build()
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
                .tilgangTilBruker(fnr)
                .get {
                    val context = lagSendHenvendelseContext(fnr, request)
                    val gsakSaker = sakerService.hentSammensatteSaker(fnr)
                    val psakSaker = sakerService.hentPensjonSaker(fnr)
                    val saker = gsakSaker.union(psakSaker)

                    val valgtSak = saker
                            .find { it.saksId == sporsmalsRequest.saksID }
                            ?: throw WebApplicationException("Fant ingen sak med id: ${sporsmalsRequest.saksID}", 400)

                    henvendelseUtsendingService.sendHenvendelse(lagSporsmal(sporsmalsRequest, valgtSak.temaKode, context), Optional.empty(), Optional.of(valgtSak), context.enhet)
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
                .tilgangTilBruker(fnr)
                .get {
                    val traadId = opprettHenvendelseRequest.traadId
                    val context = lagSendHenvendelseContext(fnr, request)
                    val traad = henvendelseService
                            .hentMeldinger(fnr, context.enhet)
                            .traader
                            .find { it.traadId == traadId }
                            ?: throw WebApplicationException("Fant ingen tråd med id: $traadId", 400)

                    val oppgaveId: String? = if (erEnkeltstaendeSporsmalFraBruker(traad)) {
                        val sporsmal = traad.meldinger[0]
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
                .tilgangTilBruker(fnr)
                .get {
                    val context = lagSendHenvendelseContext(fnr, request)
                    val traad = henvendelseService
                            .hentMeldinger(fnr, context.enhet)
                            .traader
                            .find { it.traadId == fortsettDialogRequest.traadId }
                            ?: throw WebApplicationException("Fant ingen tråd med id: ${fortsettDialogRequest.traadId}", 400)

                    val valgtSak = fortsettDialogRequest.saksId
                            ?.let {
                                val saker = hentSaker(fnr)

                                saker
                                        .find { it.saksId == fortsettDialogRequest.saksId }
                                        ?: throw WebApplicationException("Fant ingen sak med id: ${fortsettDialogRequest.saksId}", 400)
                            }

                    henvendelseUtsendingService.ferdigstillHenvendelse(
                            lagFortsettDialog(fortsettDialogRequest, context, traad),
                            Optional.ofNullable(fortsettDialogRequest.oppgaveId),
                            Optional.ofNullable(valgtSak),
                            fortsettDialogRequest.behandlingsId,
                            context.enhet
                    )

                    Response.ok().build()
                }
    }

    private fun hentSaker(fnr: String): Set<Sak> {
        val gsakSaker = try {
            sakerService.hentSammensatteSaker(fnr)
        } catch (e: Exception) {
            emptyList<Sak>()
        }

        val psakSaker = try {
            sakerService.hentPensjonSaker(fnr)
        } catch (e: Exception) {
            emptyList<Sak>()
        }

        return gsakSaker.union(psakSaker)
    }
}

private fun erEnkeltstaendeSporsmalFraBruker(traad: Traad): Boolean {
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

private fun lagFortsettDialog(request: FortsettDialogRequest, requestContext: RequestContext, traad: Traad): Melding {
    val eldsteMelding = traad.meldinger[0]
    return Melding()
            .withFnr(requestContext.fnr)
            .withNavIdent(requestContext.ident)
            .withEksternAktor(requestContext.ident)
            .withKanal(getKanal(request.meldingstype))
            .withType(request.meldingstype)
            .withFritekst(Fritekst(request.fritekst))
            .withTilknyttetEnhet(requestContext.enhet)
            .withErTilknyttetAnsatt(request.erOppgaveTilknyttetAnsatt)
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
    return when(type) {
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
        val saksID: String,
        val erOppgaveTilknyttetAnsatt: Boolean
)

data class FortsettDialogRequest(
        val traadId: String,
        val behandlingsId: String,
        val fritekst: String,
        val saksId: String?,
        val erOppgaveTilknyttetAnsatt: Boolean,
        val meldingstype: Meldingstype,
        val oppgaveId: String?
)

fun lagSendHenvendelseContext(fnr: String, request: HttpServletRequest): RequestContext {
    val ident = SubjectHandler.getSubjectHandler().uid
    val enhet = RestUtils.hentValgtEnhet(request)

    require(ident != null)
    require(enhet != null)
    return RequestContext(fnr, ident, enhet)
}

data class RequestContext(
        val fnr: String,
        val ident: String,
        val enhet: String
)