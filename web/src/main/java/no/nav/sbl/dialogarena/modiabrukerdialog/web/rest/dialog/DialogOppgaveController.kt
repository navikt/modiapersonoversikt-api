package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.GsakKodeTema
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveRestClient
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.BehandlingsIdTilgangData
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.CREATE
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import no.nav.sbl.dialogarena.naudit.AuditResources.Person.Henvendelse
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils.arbeidsdagerFraDato
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils.arbeidsdagerFraDatoJava
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgave
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgaveRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

private const val HENVENDELSESTYPE_KODE: String = "DIALOG"

@RestController
@RequestMapping("/rest/dialogoppgave")
class DialogOppgaveController @Autowired constructor(
        private val gsakKodeverk: GsakKodeverk,
        private val oppgavebehandling: OppgavebehandlingV3,
        private val oppgavebehandlingRest: OppgaveRestClient,
        private val tilgangskontroll: Tilgangskontroll
) {

    @PostMapping("/opprett")
    fun opprettOppgave(@RequestBody request: OpperettOppgaveRequest): ResponseEntity<Void> {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(request.fnr))
                .check(Policies.behandlingsIderTilhorerBruker.with(BehandlingsIdTilgangData(request.fnr, listOf(request.behandlingskjedeId))))
                .get(Audit.describe(CREATE, Henvendelse.Oppgave.Opprett, AuditIdentifier.FNR to request.fnr, AuditIdentifier.BEHANDLING_ID to request.behandlingskjedeId)) {
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
                    ResponseEntity(HttpStatus.OK)
                }
    }

    @PostMapping("/opprettskjermetoppgave")
    fun opprettSkjermetOppgave(@RequestBody request: OpperettSkjermetOppgaveDTO
    ): SkjermetOppgaveRespons {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.describe(CREATE, Henvendelse.Oppgave.Opprett, AuditIdentifier.FNR to request.fnr)) {
                    val respons = oppgavebehandlingRest
                            .opprettSkjermetOppgave(OppgaveRequest(
                                    fnr = request.fnr,
                                    behandlesAvApplikasjon = "FS22",
                                    beskrivelse = request.beskrivelse,
                                    temagruppe = "", //not in use
                                    tema = request.temaKode,
                                    underkategoriKode = request.underkategoriKode,
                                    oppgavetype = request.oppgaveTypeKode,
                                    behandlingstype = HENVENDELSESTYPE_KODE,
                                    prioritet = request.prioritetKode,
                                    opprettetavenhetsnummer = request.opprettetavenhetsnummer,
                                    oppgaveFrist = kalkulerFrist(request.temaKode, request.oppgaveTypeKode)
                            )
                    )

                    SkjermetOppgaveRespons(oppgaveid = respons.id)
                }
    }

    @GetMapping("/tema")
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

    private fun kalkulerFrist(temaKode: String, oppgaveTypeKode: String): LocalDate {
        val dagerFrist = gsakKodeverk.hentTemaListe()
                .find { it.kode == temaKode }
                ?.oppgaveTyper
                ?.find { it.kode == oppgaveTypeKode }
                ?.dagerFrist
                ?: 2

        return arbeidsdagerFraDatoJava(dagerFrist, LocalDate.now())
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
        val opprettetavenhetsnummer: String,
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
        val opprettetavenhetsnummer: String,
        val fnr: String,
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
