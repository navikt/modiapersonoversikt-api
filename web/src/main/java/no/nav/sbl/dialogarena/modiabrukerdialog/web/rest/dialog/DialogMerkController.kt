package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog

import no.nav.brukerdialog.security.context.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkUtils
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType
import java.util.*
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Path("/dialogmerking")
class DialogMerkController @Inject constructor(private val behandleHenvendelsePortType: BehandleHenvendelsePortType,
                                               private val oppgaveBehandlingService: OppgaveBehandlingService) {

    @POST
    @Path("/feilsendt")
    fun merkSomFeilsendt(request: FeilmerkRequest): Response {
        // TODO tilgangsstyring
        behandleHenvendelsePortType.oppdaterTilKassering(request.behandlingsidListe)
        return Response.ok().build()
    }

    @POST
    @Path("/bidrag")
    fun merkSomBidrag(request: BidragRequest): Response {
        // TODO tilgangsstyring
        behandleHenvendelsePortType.knyttBehandlingskjedeTilTema(request.eldsteMeldingTraadId, "BID")
        return Response.ok().build()
    }

    @POST
    @Path("/kontorsperret")
    fun merkSomKontorsperret(request: KontorsperretRequest): Response {
        // TODO tilgangsstyring
        behandleHenvendelsePortType.oppdaterKontorsperre(request.fnr, request.meldingsidListe)
        return Response.ok().build()
    }

    @POST
    @Path("/avslutt")
    fun avsluttUtenSvar(request: AvsluttUtenSvarRequest): Response {
        // TODO tilgangsstyring
        behandleHenvendelsePortType.ferdigstillUtenSvar(request.eldsteMeldingTraadId, request.saksbehandlerValgtEnhet)
        oppgaveBehandlingService.ferdigstillOppgaveIGsak(request.eldsteMeldingOppgaveId, Optional.empty(), request.saksbehandlerValgtEnhet)
        return Response.ok().build()
    }

    @POST
    @Path("/slett")
    fun slettBehandlingskjede(request: FeilmerkRequest): Response {
        // TODO tilgangsstyring
        if (MerkUtils.kanHastekassere(SubjectHandler.getSubjectHandler().getUid())) {
            behandleHenvendelsePortType.markerTraadForHasteKassering(request.behandlingsidListe);
            return Response.ok().build()
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build()
        }
    }

}

data class FeilmerkRequest(val behandlingsidListe: List<String>)

data class BidragRequest(val eldsteMeldingTraadId: String)

data class KontorsperretRequest(val fnr: String,
                                val meldingsidListe: List<String>)

data class AvsluttUtenSvarRequest(val saksbehandlerValgtEnhet: String,
                                  val eldsteMeldingTraadId: String,
                                  val eldsteMeldingOppgaveId: String)
