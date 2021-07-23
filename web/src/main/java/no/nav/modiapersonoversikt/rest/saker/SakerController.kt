package no.nav.modiapersonoversikt.rest.saker

import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.READ
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.api.utils.RestUtils
import no.nav.modiapersonoversikt.legacy.sak.domain.widget.ModiaSakstema
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.*
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Dokument.Variantformat
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Dokument.Variantformat.ARKIV
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper
import no.nav.modiapersonoversikt.legacy.sak.service.DokumentMetadataService
import no.nav.modiapersonoversikt.legacy.sak.service.SaksService
import no.nav.modiapersonoversikt.legacy.sak.service.SakstemaService
import no.nav.modiapersonoversikt.legacy.sak.service.interfaces.SaksoversiktService
import no.nav.modiapersonoversikt.legacy.sak.service.interfaces.TilgangskontrollService
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafService
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/rest/saker/{fnr}")
class SakerController @Autowired constructor(
    private val saksoversiktService: SaksoversiktService,
    private val sakstemaService: SakstemaService,
    private val saksService: SaksService,
    private val tilgangskontrollService: TilgangskontrollService,
    private val dokumentMetadataService: DokumentMetadataService,
    private val safService: SafService,
    val tilgangskontroll: Tilgangskontroll
) {
    @GetMapping("/sakstema")
    fun hentSakstema(request: HttpServletRequest, @PathVariable("fnr") fnr: String, @RequestParam(value = "enhet", required = false) enhet: String?): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(READ, AuditResources.Person.Saker, AuditIdentifier.FNR to fnr)) {
                val sakerWrapper = saksService.hentAlleSaker(fnr)
                val sakstemaWrapper = sakstemaService.hentSakstema(sakerWrapper.resultat, fnr, false)

                // TODO skal denne metoden ligge i tilgangskontrollService?
                tilgangskontrollService.markerIkkeJournalforte(sakstemaWrapper.resultat)
                saksoversiktService.fjernGamleDokumenter(sakstemaWrapper.resultat)

                val resultat = ResultatWrapper(
                    mapTilModiaSakstema(sakstemaWrapper.resultat, RestUtils.hentValgtEnhet(enhet, request)),
                    collectFeilendeSystemer(sakerWrapper, sakstemaWrapper)
                )

                byggSakstemaResultat(resultat)
            }
    }

    @GetMapping(value = ["/dokument/{journalpostId}/{dokumentreferanse}"], produces = ["application/pdf"])
    fun hentDokument(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @PathVariable("journalpostId") journalpostId: String,
        @PathVariable("dokumentreferanse") dokumentreferanse: String
    ): ResponseEntity<Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(READ, AuditResources.Person.Dokumenter, AuditIdentifier.FNR to fnr, AuditIdentifier.JOURNALPOST_ID to journalpostId, AuditIdentifier.DOKUMENT_REFERERANSE to dokumentreferanse)) {
                val journalpostMetadata = hentDokumentMetadata(journalpostId, fnr)
                val tilgangskontrollResult = tilgangskontrollService.harSaksbehandlerTilgangTilDokument(
                    request,
                    journalpostMetadata,
                    fnr,
                    journalpostMetadata.temakode
                )

                // TODO erstatt tilgangsstyring
                if (!tilgangskontrollResult.result.isPresent || !finnesDokumentReferansenIMetadata(journalpostMetadata, dokumentreferanse)) {
                    throw ResponseStatusException(HttpStatus.FORBIDDEN)
                } else {
                    val variantformat = finnVariantformat(journalpostMetadata, dokumentreferanse)

                    safService.hentDokument(journalpostId, dokumentreferanse, variantformat).let { wrapper ->
                        wrapper.result
                            .map { ResponseEntity(it, HttpStatus.OK) }
                            .orElseGet { ResponseEntity(HttpStatus.valueOf(wrapper.statuskode)) }
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
                    "tilhørendeSaker" to hentTilhorendeSaker(it.tilhorendeSaker),
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
        return dokumenter.filterNot { it.baksystem == Baksystem.HENVENDELSE && it.journalpostId == null && it.tilhorendeSakid == null }
            .map {
                mapOf(
                    "id" to unikId(),
                    "retning" to it.retning,
                    "dato" to hentDato(it.dato),
                    "navn" to it.navn,
                    "journalpostId" to it.journalpostId,
                    "hoveddokument" to hentDokument(it.hoveddokument),
                    "vedlegg" to it.vedlegg.map { vedlegg -> hentDokument(vedlegg) },
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

    private fun hentTilhorendeSaker(saker: List<Sak>): List<Map<String, Any?>> {
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
