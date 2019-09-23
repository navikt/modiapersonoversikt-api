package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog

import no.nav.brukerdialog.security.context.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.*
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkUtils
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType
import java.util.*
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Path("/dialogmerking")
class DialogMerkController @Inject constructor(private val behandleHenvendelsePortType: BehandleHenvendelsePortType,
                                               private val oppgaveBehandlingService: OppgaveBehandlingService,
                                               private val tilgangskontroll: Tilgangskontroll
) {

    @POST
    @Path("/feilsendt")
    fun merkSomFeilsendt(request: FeilmerkRequest): Response {
        return tilgangskontroll
                .tilgangTilBruker(request.fnr)
                .behandlingsIderTilhorerFnr(request.fnr, request.behandlingsidListe)
                .get {
                    behandleHenvendelsePortType.oppdaterTilKassering(request.behandlingsidListe)
                    Response.ok().build()
                }
    }

    @POST
    @Path("/bidrag")
    fun merkSomBidrag(request: BidragRequest): Response {
        return tilgangskontroll
                .tilgangTilBruker(request.fnr)
                .behandlingsIderTilhorerFnr(request.fnr, listOf(request.eldsteMeldingTraadId))
                .get {
                    behandleHenvendelsePortType.knyttBehandlingskjedeTilTema(request.eldsteMeldingTraadId, "BID")
                    Response.ok().build()
                }
    }

    @POST
    @Path("/kontorsperret")
    fun merkSomKontorsperret(request: KontorsperretRequest): Response {
        return tilgangskontroll
                .tilgangTilBruker(request.fnr)
                .behandlingsIderTilhorerFnr(request.fnr, request.meldingsidListe)
                .get {
                    behandleHenvendelsePortType.oppdaterKontorsperre(request.fnr, request.meldingsidListe)
                    Response.ok().build()
                }
    }

    @POST
    @Path("/avslutt")
    fun avsluttUtenSvar(request: AvsluttUtenSvarRequest): Response {
        return tilgangskontroll
                .tilgangTilBruker(request.fnr)
                .behandlingsIderTilhorerFnr(request.fnr, listOf(request.eldsteMeldingTraadId))
                .get {
                    behandleHenvendelsePortType.ferdigstillUtenSvar(request.eldsteMeldingTraadId, request.saksbehandlerValgtEnhet)
                    oppgaveBehandlingService.ferdigstillOppgaveIGsak(request.eldsteMeldingOppgaveId, Optional.empty(), request.saksbehandlerValgtEnhet)
                    Response.ok().build()
                }
    }

    @POST
    @Path("/slett")
    fun slettBehandlingskjede(request: FeilmerkRequest): Response {
        return tilgangskontroll
                .saksbehandlerKanHastekassere()
                .tilgangTilBruker(request.fnr)
                .behandlingsIderTilhorerFnr(request.fnr, request.behandlingsidListe)
                .get {
                    if (MerkUtils.kanHastekassere(SubjectHandler.getSubjectHandler().getUid())) {
                        behandleHenvendelsePortType.markerTraadForHasteKassering(request.behandlingsidListe);
                        Response.ok().build()
                    } else {
                        Response.status(Response.Status.UNAUTHORIZED).build()
                    }
                }
    }

}

data class FeilmerkRequest(val fnr: String, val behandlingsidListe: List<String>)

data class BidragRequest(val fnr: String, val eldsteMeldingTraadId: String)

data class KontorsperretRequest(val fnr: String, val meldingsidListe: List<String>)

data class AvsluttUtenSvarRequest(
        val fnr: String,
        val saksbehandlerValgtEnhet: String,
        val eldsteMeldingTraadId: String,
        val eldsteMeldingOppgaveId: String
)
