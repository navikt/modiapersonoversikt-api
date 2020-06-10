package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.GsakKodeTema
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveRestClient
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.BehandlingsIdTilgangData
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.*
import no.nav.sbl.dialogarena.naudit.AuditResources.Person.Henvendelse
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils.arbeidsdagerFraDato
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgave
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgaveRequest
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils.arbeidsdagerFraDatoJava
import java.time.LocalDate.*
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response

private const val HENVENDELSESTYPE_KODE: String = "DIALOG"

@Path("/dialogoppgave")
class DialogOppgaveController @Inject constructor(
        private val gsakKodeverk: GsakKodeverk,
        private val oppgavebehandling: OppgavebehandlingV3,
        private val oppgavebehandlingRest: OppgaveRestClient,
        private val tilgangskontroll: Tilgangskontroll
) {

    @POST
    @Path("/opprett")
    fun opprettOppgave(request: OpperettOppgaveRequest): Response {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(request.fnr))
                .check(Policies.behandlingsIderTilhorerBruker.with(BehandlingsIdTilgangData(request.fnr, listOf(request.behandlingskjedeId))))
                .get(Audit.describe(CREATE, Henvendelse.Oppgave.Opprett, "fnr" to request.fnr, "behandlingsIder" to request.behandlingskjedeId)) {
                    oppgavebehandling.opprettOppgave(
                            WSOpprettOppgaveRequest()
                                    .withOpprettetAvEnhetId(request.valgtEnhetId)
                                    .withHenvendelsetypeKode(HENVENDELSESTYPE_KODE)
                                    .withOpprettOppgave(
                                            WSOpprettOppgave()
                                                    .withHenvendelseId(request.behandlingskjedeId)
                                                    .withAktivFra(org.joda.time.LocalDate.now())
                                                    .withAktivTil(arbeidsdagerFraDato(request.dagerFrist, org.joda.time.LocalDate.now()))
                                                    .withAnsvarligEnhetId(request.ansvarligEnhetId)
                                                    .withAnsvarligId(request.ansvarligIdent)
                                                    .withBeskrivelse(request.beskrivelse)
                                                    .withFagomradeKode(request.temaKode)
                                                    .withUnderkategoriKode(request.underkategoriKode)
                                                    .withBrukerId(request.brukerid)
                                                    .withOppgavetypeKode(request.oppgaveTypeKode)
                                                    .withPrioritetKode(request.prioritetKode)
                                                    .withLest(false)
                                    )
                    )
                    Response.ok().build()
                }
    }

    @POST
    @Path("/opprettskjermetoppgave")
    fun opprettSkjermetOppgave(request: OpperettSkjermetOppgaveDTO
    ): SkjermetOppgaveRespons {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.describe(CREATE, Henvendelse.Oppgave.Opprett, "fnr" to request.fnr)) {
                    val respons = oppgavebehandlingRest
                            .opprettOppgave(OppgaveRequest(
                                    opprettetAvEnhetsnr = request.valgtEnhetId,
                                    fnr = request.fnr,
                                    behandlesAvApplikasjon = "FS22",
                                    beskrivelse = request.beskrivelse,
                                    temagruppe = request.temaKode,
                                    tema = request.temaKode,
                                    underkategoriKode = request.underkategoriKode,
                                    oppgavetype = request.oppgaveTypeKode,
                                    behandlingstype = HENVENDELSESTYPE_KODE,
                                    aktivDato = now(),
                                    fristFerdigstillelse = arbeidsdagerFraDatoJava(request.dagerFrist, now()),
                                    prioritet = request.prioritetKode
                            )
                            )
                    SkjermetOppgaveRespons(
                            oppgaveid = respons.oppgaveid
                    )
                }
    }

    @GET
    @Path("/tema")
    @Produces(APPLICATION_JSON)
    fun hentAlleTema(): List<Map<String, Any?>> {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.skipAuditLog()) {
                    val gsakTemaListe = gsakKodeverk.hentTemaListe()
                    gsakTemaListe.filter { it.oppgaveTyper.isNotEmpty() }.map {
                        mapOf(
                                *hentGsakKodeTema(it),
                                "oppgavetyper" to hentOppgavetyper(it.oppgaveTyper),
                                "prioriteter" to hentPrioriteter(it.prioriteter),
                                "underkategorier" to hentUnderkategorier(it.underkategorier)
                        )
                    }
                }
    }

    private fun hentOppgavetyper(oppgavetyper: List<GsakKodeTema.OppgaveType>): List<Map<String, Any?>> =
            oppgavetyper.map {
                mapOf(
                        *hentGsakKodeTema(it),
                        "dagerFrist" to it.dagerFrist
                )
            }

    private fun hentPrioriteter(prioriteter: List<GsakKodeTema.Prioritet>): List<Map<String, Any?>> =
            prioriteter.map {
                mapOf(
                        *hentGsakKodeTema(it)
                )
            }

    private fun hentUnderkategorier(underkategorier: List<GsakKodeTema.Underkategori>): List<Map<String, Any?>> =
            underkategorier.map {
                mapOf(
                        *hentGsakKodeTema(it),
                        "erGyldig" to it.erGyldig()
                )
            }

    private fun hentGsakKodeTema(kodeTema: GsakKodeTema): Array<Pair<String, Any?>> =
            arrayOf(Pair("kode", kodeTema.kode), Pair("tekst", kodeTema.tekst))

}

data class OpperettOppgaveRequest(
        val fnr: String,
        val valgtEnhetId: Int,
        val behandlingskjedeId: String,
        val dagerFrist: Int,
        val ansvarligEnhetId: String,
        val ansvarligIdent: String?,
        val beskrivelse: String,
        val temaKode: String,
        val underkategoriKode: String?,
        val brukerid: String,
        val oppgaveTypeKode: String,
        val prioritetKode: String
)

data class OpperettSkjermetOppgaveDTO(
        val fnr: String,
        val valgtEnhetId: String,
        val dagerFrist: Int,
        val beskrivelse: String,
        val temaKode: String,
        val underkategoriKode: String?,
        val brukerid: String,
        val oppgaveTypeKode: String,
        val prioritetKode: String
)

data class SkjermetOppgaveRespons(
        val oppgaveid: String
)