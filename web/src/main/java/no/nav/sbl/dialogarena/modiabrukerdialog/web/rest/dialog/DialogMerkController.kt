package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog

import no.nav.common.auth.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.BehandlingsIdTilgangData
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.*
import no.nav.sbl.dialogarena.naudit.AuditResources.Person.Henvendelse
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType
import java.util.*
import javax.inject.Inject
import javax.ws.rs.GET
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
                .check(Policies.tilgangTilBruker.with(request.fnr))
                .check(Policies.behandlingsIderTilhorerBruker.with(BehandlingsIdTilgangData(request.fnr, request.behandlingsidListe)))
                .get(Audit.describe(UPDATE, Henvendelse.Merk.Feilsendt, "fnr" to request.fnr, "behandlingsIder" to request.behandlingsidListe.joinToString(", "))) {
                    behandleHenvendelsePortType.oppdaterTilKassering(request.behandlingsidListe)
                    Response.ok().build()
                }
    }

    @POST
    @Path("/bidrag")
    fun merkSomBidrag(request: BidragRequest): Response {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(request.fnr))
                .check(Policies.behandlingsIderTilhorerBruker.with(BehandlingsIdTilgangData(request.fnr, listOf(request.eldsteMeldingTraadId))))
                .get(Audit.describe(UPDATE, Henvendelse.Merk.Bidrag, "fnr" to request.fnr, "behandlingsIder" to request.eldsteMeldingTraadId)) {
                    behandleHenvendelsePortType.knyttBehandlingskjedeTilTema(request.eldsteMeldingTraadId, "BID")
                    Response.ok().build()
                }
    }

    @POST
    @Path("/kontorsperret")
    fun merkSomKontorsperret(request: KontorsperretRequest): Response {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(request.fnr))
                .check(Policies.behandlingsIderTilhorerBruker.with(BehandlingsIdTilgangData(request.fnr, request.meldingsidListe)))
                .get(Audit.describe(UPDATE, Henvendelse.Merk.Kontorsperre, "fnr" to request.fnr, "behandlingsIder" to request.meldingsidListe.joinToString(", "))) {
                    behandleHenvendelsePortType.oppdaterKontorsperre(request.fnr, request.meldingsidListe)
                    Response.ok().build()
                }
    }

    @POST
    @Path("/avslutt")
    fun avsluttUtenSvar(request: AvsluttUtenSvarRequest): Response {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(request.fnr))
                .check(Policies.behandlingsIderTilhorerBruker.with(BehandlingsIdTilgangData(request.fnr, listOf(request.eldsteMeldingTraadId))))
                .get(Audit.describe(UPDATE, Henvendelse.Merk.Avslutt, "fnr" to request.fnr, "behandlingsIder" to request.eldsteMeldingTraadId)) {
                    behandleHenvendelsePortType.ferdigstillUtenSvar(request.eldsteMeldingTraadId, request.saksbehandlerValgtEnhet)
                    oppgaveBehandlingService.ferdigstillOppgaveIGsak(request.eldsteMeldingOppgaveId, Optional.empty(), request.saksbehandlerValgtEnhet)
                    Response.ok().build()
                }
    }

    @POST
    @Path("/avsluttgosysoppgave")
    fun avsluttGosysOppgave(request: FerdigstillOppgaveRequest): Response {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(request.fnr))
                .get(Audit.describe(UPDATE, Henvendelse.Oppgave.Avslutt, "fnr" to request.fnr, "oppgaveid" to request.oppgaveid)) {
                    oppgaveBehandlingService.ferdigstillGsakOppgave(request.oppgaveid, Optional.empty(), request.saksbehandlerValgtEnhet, request.beskrivelse);
                    Response.ok().build()
                }
    }

    @POST
    @Path("/slett")
    fun slettBehandlingskjede(request: FeilmerkRequest): Response {
        return tilgangskontroll
                .check(Policies.kanHastekassere)
                .check(Policies.tilgangTilBruker.with(request.fnr))
                .check(Policies.behandlingsIderTilhorerBruker.with(BehandlingsIdTilgangData(request.fnr, request.behandlingsidListe)))
                .get(Audit.describe(DELETE, Henvendelse.Merk.Slett, "fnr" to request.fnr, "behandlingsIder" to request.behandlingsidListe.joinToString(", "))) {
                        behandleHenvendelsePortType.markerTraadForHasteKassering(request.behandlingsidListe);
                        Response.ok().build()
                }
    }

    @GET
    @Path("/slett")
    fun kanSlette(): Response {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.skipAuditLog()) {
                    val godkjenteSaksbehandlere = tilgangskontroll.context().hentSaksbehandlereMedTilgangTilHastekassering()
                    val saksbehandlerId = SubjectHandler.getIdent().map(String::toUpperCase).get()
                    Response.ok(godkjenteSaksbehandlere.contains(saksbehandlerId)).build()
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

data class FerdigstillOppgaveRequest(
        val fnr: String,
        val oppgaveid: String,
        val beskrivelse: String,
        val saksbehandlerValgtEnhet: String
)