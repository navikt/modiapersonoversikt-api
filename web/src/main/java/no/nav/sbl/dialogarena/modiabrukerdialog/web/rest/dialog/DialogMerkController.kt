package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog

import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService
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
        behandleHenvendelsePortType.oppdaterTilKassering(request.behandlingsidListe)
        return Response.ok().build()
    }

    @POST
    @Path("/bidrag")
    fun merkSomBidrag(request: BidragRequest): Response {
        behandleHenvendelsePortType.knyttBehandlingskjedeTilTema(request.eldsteMeldingTraadId, "BID")
        return Response.ok().build()
    }

    @POST
    @Path("/kontorsperret")
    fun merkSomKontorsperret(request: KontorsperretRequest): Response {
        behandleHenvendelsePortType.oppdaterKontorsperre(request.fnr, request.meldingsidListe)
        return Response.ok().build()
    }

    @POST
    @Path("/avslutt")
    fun avsluttUtenSvar(request: AvsluttUtenSvarRequest): Response {
        behandleHenvendelsePortType.ferdigstillUtenSvar(request.eldsteMeldingTraadId, request.saksbehandlerValgteEnhet)
        oppgaveBehandlingService.ferdigstillOppgaveIGsak(request.eldsteMeldingOppgaveId, Optional.empty(), request.saksbehandlerValgteEnhet)
        return Response.ok().build()
    }

}

data class FeilmerkRequest(val behandlingsidListe: List<String>)

data class BidragRequest(val eldsteMeldingTraadId: String)

data class KontorsperretRequest(val fnr: String,
                                val meldingsidListe: List<String>)

data class AvsluttUtenSvarRequest(val saksbehandlerValgtEnhet: String,
                                  val eldsteMeldingTraadId: String,
                                  val eldsteMeldingOppgaveId: String)
