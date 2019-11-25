package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.saker

import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.domain.widget.ModiaSakstema
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.*
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Dokument.Variantformat
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Dokument.Variantformat.ARKIV
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.DokumentMetadataService
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.SaksService
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.SakstemaService
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.SaksoversiktService
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.TilgangskontrollService
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.saf.SafService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.AuditResources
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.*
import org.joda.time.DateTime
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/saker/{fnr}")
class SakerController @Inject constructor(private val saksoversiktService: SaksoversiktService,
                                          private val sakstemaService: SakstemaService,
                                          private val saksService: SaksService,
                                          private val tilgangskontrollService: TilgangskontrollService,
                                          private val dokumentMetadataService: DokumentMetadataService,
                                          private val safService: SafService,
                                          val tilgangskontroll: Tilgangskontroll
) {
    @GET
    @Path("/sakstema")
    @Produces(MediaType.APPLICATION_JSON)
    fun hentSakstema(@Context request: HttpServletRequest, @PathParam("fnr") fnr: String): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(READ, AuditResources.Person.Saker, "fnr" to fnr)) {
                val sakerWrapper = saksService.hentAlleSaker(fnr)
                val sakstemaWrapper = sakstemaService.hentSakstema(sakerWrapper.resultat, fnr, false)

                // TODO skal denne metoden ligge i tilgangskontrollService?
                tilgangskontrollService.markerIkkeJournalforte(sakstemaWrapper.resultat)
                saksoversiktService.fjernGamleDokumenter(sakstemaWrapper.resultat)

                val resultat = ResultatWrapper(mapTilModiaSakstema(sakstemaWrapper.resultat, RestUtils.hentValgtEnhet(request)),
                        collectFeilendeSystemer(sakerWrapper, sakstemaWrapper))

                byggSakstemaResultat(resultat)
            }
    }

    @GET
    @Path("/dokument/{journalpostId}/{dokumentreferanse}")
    @Produces("application/pdf")
    fun hentDokument(@Context request: HttpServletRequest, @PathParam("fnr") fnr: String,
                     @PathParam("journalpostId") journalpostId: String,
                     @PathParam("dokumentreferanse") dokumentreferanse: String): Response {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get(Audit.describe(READ, AuditResources.Person.Dokumenter, "fnr" to fnr, "journalpostId" to journalpostId, "dokumentreferanse" to dokumentreferanse)) {
                    val journalpostMetadata = hentDokumentMetadata(journalpostId, fnr)
                    val tilgangskontrollResult = tilgangskontrollService.harSaksbehandlerTilgangTilDokument(request,
                            journalpostMetadata, fnr, journalpostMetadata.temakode)

                    // TODO erstatt tilgangsstyring
                    if (!tilgangskontrollResult.result.isPresent || !finnesDokumentReferansenIMetadata(journalpostMetadata, dokumentreferanse)) {
                        Response.status(Response.Status.FORBIDDEN).build()
                    } else {
                        val variantformat = finnVariantformat(journalpostMetadata, dokumentreferanse)

                        safService.hentDokument(journalpostId, dokumentreferanse, variantformat).let { wrapper ->
                            wrapper.result
                                    .map { Response.ok(it).build() }
                                    .orElseGet { Response.status(wrapper.statuskode).build() }
                        }
                    }

                }
    }

    private fun finnVariantformat(journalpostMetadata: DokumentMetadata, dokumentreferanse: String): Variantformat =
            journalpostMetadata.vedlegg.plus(journalpostMetadata.hoveddokument)
                    .find { dok -> dok.dokumentreferanse == dokumentreferanse }
                    ?.variantformat
                    ?: ARKIV


    private fun byggSakstemaResultat(resultat: ResultatWrapper<List<ModiaSakstema>>): Map<String, Any?> {
        return mapOf(
                "resultat" to resultat.resultat.map {
                    mapOf(
                            "temakode" to it.temakode,
                            "temanavn" to it.temanavn,
                            "erGruppert" to it.erGruppert,
                            "behandlingskjeder" to hentBehandlingskjeder(it.behandlingskjeder),
                            "dokumentMetadata" to hentDokumentMetadata(it.dokumentMetadata),
                            "tilhørendeSaker" to hentTilhørendeSaker(it.tilhorendeSaker),
                            "feilkoder" to it.feilkoder,
                            "harTilgang" to it.harTilgang
                    )
                }
        )
    }

    private fun hentBehandlingskjeder(behandlingskjeder: List<Behandlingskjede>): List<Map<String, Any?>> {
        return behandlingskjeder.map {
            mapOf(
                    "status" to it.status,
                    "sistOppdatert" to hentDato(it.sistOppdatert)
            )
        }
    }

    private fun hentDokumentMetadata(dokumenter: List<DokumentMetadata>): List<Map<String, Any?>> {
        return dokumenter.map {
            mapOf(
                    "id" to unikId(),
                    "retning" to it.retning,
                    "dato" to hentDato(it.dato),
                    "navn" to it.navn,
                    "journalpostId" to it.journalpostId,
                    "hoveddokument" to hentDokument(it.hoveddokument),
                    "vedlegg" to it.vedlegg.map { hentDokument(it) },
                    "avsender" to it.avsender,
                    "mottaker" to it.mottaker,
                    "tilhørendeSaksid" to it.tilhorendeSakid,
                    "tilhørendeFagsaksid" to it.tilhorendeFagsakId,
                    "behandlingsid" to it.behandlingsId,
                    "baksystem" to it.baksystem,
                    "temakode" to it.temakode,
                    "temakodeVisning" to it.temakodeVisning,
                    "ettersending" to it.isEttersending,
                    "erJournalført" to it.isErJournalfort,
                    "feil" to mapOf(
                            "inneholderFeil" to it.feilWrapper?.inneholderFeil,
                            "feilmelding" to it.feilWrapper?.feilmelding
                    )
            )
        }
    }

    private fun hentTilhørendeSaker(saker: List<Sak>): List<Map<String, Any?>> {
        return saker.map {
            mapOf(
                    "temakode" to it.temakode,
                    "saksid" to it.saksId,
                    "fagsaksnummer" to it.fagsaksnummer,
                    "avsluttet" to hentDato(it.avsluttet),
                    "fagsystem" to it.fagsystem,
                    "baksystem" to it.baksystem
            )
        }
    }

    private fun hentDokument(dokument: Dokument): Map<String, Any?> {
        return mapOf(
                "tittel" to dokument.tittel,
                "dokumentreferanse" to dokument.dokumentreferanse,
                "kanVises" to dokument.isKanVises,
                "logiskDokument" to dokument.isLogiskDokument,
                "skjerming" to dokument.skjerming
        )
    }

    private fun hentDato(date: LocalDateTime): Map<String, Any?> {
        return mapOf(
                "år" to date.year,
                "måned" to date.monthValue,
                "dag" to date.dayOfMonth,
                "time" to date.hour,
                "minutt" to date.minute,
                "sekund" to date.second
        )
    }

    private fun hentDato(date: Optional<DateTime>): Map<String, Any?>? {
        if (!date.isPresent) return null
        return mapOf(
                "år" to date.get().year,
                "måned" to date.get().monthOfYear,
                "dag" to date.get().dayOfMonth,
                "time" to date.get().hourOfDay,
                "minutt" to date.get().minuteOfHour,
                "sekund" to date.get().secondOfMinute
        )
    }

    private fun mapTilModiaSakstema(sakstemaList: List<Sakstema>, valgtEnhet: String): List<ModiaSakstema> {
        return sakstemaList.map { sakstema -> createModiaSakstema(sakstema, valgtEnhet) }
    }

    private fun collectFeilendeSystemer(sakerWrapper: ResultatWrapper<List<Sak>>, sakstemaWrapper: ResultatWrapper<List<Sakstema>>): Set<Baksystem> {
        return sakerWrapper.feilendeSystemer.union(sakstemaWrapper.feilendeSystemer)
    }

    private fun createModiaSakstema(sakstema: Sakstema, valgtEnhet: String): ModiaSakstema {
        return ModiaSakstema(sakstema)
                .withTilgang(tilgangskontrollService.harEnhetTilgangTilTema(sakstema.temakode, valgtEnhet))
    }

    private fun hentDokumentMetadata(journalpostId: String, fnr: String): DokumentMetadata {
        return dokumentMetadataService.hentDokumentMetadata(fnr).resultat
                .first { dokumentMetadata -> journalpostId == dokumentMetadata.journalpostId }
                ?: throw RuntimeException("Fant ikke metadata om journalpostId $journalpostId. Dette bør ikke skje.")

    }

    private fun finnesDokumentReferansenIMetadata(dokumentMetadata: DokumentMetadata, dokumentreferanse: String): Boolean {
        return dokumentMetadata.hoveddokument.dokumentreferanse == dokumentreferanse ||
                dokumentMetadata.vedlegg.any { dokument -> dokument.dokumentreferanse == dokumentreferanse }
    }

    private fun unikId(): String = UUID.randomUUID().toString()
}