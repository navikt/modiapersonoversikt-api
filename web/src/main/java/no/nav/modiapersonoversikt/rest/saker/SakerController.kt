package no.nav.modiapersonoversikt.rest.saker

import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.commondomain.sak.Baksystem
import no.nav.modiapersonoversikt.commondomain.sak.Feilmelding
import no.nav.modiapersonoversikt.commondomain.sak.ResultatWrapper
import no.nav.modiapersonoversikt.commondomain.sak.TjenesteResultatWrapper
import no.nav.modiapersonoversikt.infrastructure.kabac.Decision
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.READ
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.rest.RestUtils
import no.nav.modiapersonoversikt.service.journalforingsaker.SakerService
import no.nav.modiapersonoversikt.service.saf.SafService
import no.nav.modiapersonoversikt.service.saf.domain.Dokument
import no.nav.modiapersonoversikt.service.saf.domain.Dokument.Variantformat
import no.nav.modiapersonoversikt.service.saf.domain.Dokument.Variantformat.ARKIV
import no.nav.modiapersonoversikt.service.saf.domain.DokumentMetadata
import no.nav.modiapersonoversikt.service.sakstema.SakstemaService
import no.nav.modiapersonoversikt.service.sakstema.domain.Behandlingskjede
import no.nav.modiapersonoversikt.service.sakstema.domain.Sak
import no.nav.modiapersonoversikt.service.sakstema.domain.Sakstema
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
    private val sakstemaService: SakstemaService,
    private val sakerService: SakerService,
    private val safService: SafService,
    val tilgangskontroll: Tilgangskontroll
) {
    @GetMapping("/sakstema")
    fun hentSakstema(request: HttpServletRequest, @PathVariable("fnr") fnr: String, @RequestParam(value = "enhet", required = false) enhet: String?): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(READ, AuditResources.Person.Saker, AuditIdentifier.FNR to fnr)) {
                val sakerWrapper = sakerService.hentSafSaker(fnr).asWrapper()
                val sakstemaWrapper = sakstemaService.hentSakstema(sakerWrapper.resultat, fnr)

                val resultat =
                    ResultatWrapper(
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
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(READ, AuditResources.Person.Dokumenter, AuditIdentifier.FNR to fnr, AuditIdentifier.JOURNALPOST_ID to journalpostId, AuditIdentifier.DOKUMENT_REFERERANSE to dokumentreferanse)) {
                val journalpostMetadata = hentDokumentMetadata(journalpostId, fnr)
                val tilgangskontrollResult = harTilgangTilDokument(fnr, journalpostMetadata)

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

    private fun harTilgangTilDokument(fnr: String, dokument: DokumentMetadata): TjenesteResultatWrapper {
        return if (!dokument.isErJournalfort) {
            TjenesteResultatWrapper(
                Feilmelding.IKKE_JOURNALFORT,
                mapOf(
                    "fnr" to fnr
                )
            )
        } else if (dokument.feilWrapper.inneholderFeil) {
            TjenesteResultatWrapper(
                dokument.feilWrapper.feilmelding
            )
        } else {
            TjenesteResultatWrapper(true)
        }
    }

    private fun finnVariantformat(journalpostMetadata: DokumentMetadata, dokumentreferanse: String): Variantformat =
        journalpostMetadata.vedlegg.plus(journalpostMetadata.hoveddokument)
            .find { dok -> dok.dokumentreferanse == dokumentreferanse }
            ?.variantformat
            ?: ARKIV

    private fun byggSakstemaResultat(resultat: ResultatWrapper<List<SakstemaDTO>>): Map<String, Any?> {
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
        return dokumenter
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
                    "baksystem" to it.baksystem,
                    "temakode" to it.temakode,
                    "temakodeVisning" to it.temakodeVisning,
                    "ettersending" to false,
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
            "skjerming" to dokument.skjerming,
            "erKassert" to dokument.isKassert,
            "dokumentStatus" to dokument.dokumentStatus
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

    private fun mapTilModiaSakstema(sakstemaList: List<Sakstema>, valgtEnhet: String): List<SakstemaDTO> {
        return sakstemaList.map { sakstema -> createModiaSakstema(sakstema, valgtEnhet) }
    }

    private fun collectFeilendeSystemer(sakerWrapper: ResultatWrapper<List<Sak>>, sakstemaWrapper: ResultatWrapper<List<Sakstema>>): Set<Baksystem> {
        return sakerWrapper.feilendeSystemer.union(sakstemaWrapper.feilendeSystemer)
    }

    private fun createModiaSakstema(sakstema: Sakstema, valgtEnhet: String): SakstemaDTO {
        val harTilgang = tilgangskontroll
            .check(Policies.tilgangTilTema(EnhetId(valgtEnhet), sakstema.temakode))
            .getDecision().type == Decision.Type.PERMIT
        var dokumenterMetadata = sakstema.dokumentMetadata
        if (!harTilgang) {
            dokumenterMetadata = dokumenterMetadata.map {
                it.withFeilWrapper(Feilmelding.SIKKERHETSBEGRENSNING)
            }
        }
        return SakstemaDTO(
            temakode = sakstema.temakode,
            temanavn = sakstema.temanavn,
            erGruppert = sakstema.erGruppert,
            behandlingskjeder = sakstema.behandlingskjeder,
            dokumentMetadata = dokumenterMetadata,
            tilhorendeSaker = sakstema.tilhorendeSaker,
            feilkoder = sakstema.feilkoder,
            harTilgang = harTilgang
        )
    }

    private fun hentDokumentMetadata(journalpostId: String, fnr: String): DokumentMetadata {
        return safService.hentJournalposter(fnr).resultat
            .firstOrNull { dokumentMetadata -> journalpostId == dokumentMetadata.journalpostId }
            ?: throw RuntimeException("Fant ikke metadata om journalpostId $journalpostId. Dette bør ikke skje.")
    }

    private fun finnesDokumentReferansenIMetadata(dokumentMetadata: DokumentMetadata, dokumentreferanse: String): Boolean {
        return dokumentMetadata.hoveddokument.dokumentreferanse == dokumentreferanse ||
            dokumentMetadata.vedlegg.any { dokument -> dokument.dokumentreferanse == dokumentreferanse }
    }

    private fun SakerService.Resultat.asWrapper(): ResultatWrapper<List<Sak>> {
        val saker = this.saker.map {
            Sak()
                .withSaksId(it.saksId)
                .withFagsaksnummer(it.fagsystemSaksId)
                .withTemakode(it.temaKode)
                .withBaksystem(Baksystem.SAF)
                .withFagsystem(it.fagsystemKode)
        }
        val feilendeSystemer = this.feiledeSystemer
            .map {
                runCatching {
                    Baksystem.valueOf(it)
                }.getOrNull()
            }
            .toSet()

        return ResultatWrapper(
            saker,
            feilendeSystemer
        )
    }

    private fun unikId(): String = UUID.randomUUID().toString()

    data class SakstemaDTO(
        val temakode: String,
        val temanavn: String,
        val erGruppert: Boolean,
        val behandlingskjeder: List<Behandlingskjede>,
        val dokumentMetadata: List<DokumentMetadata>,
        val tilhorendeSaker: List<Sak>,
        val feilkoder: List<Int>,
        val harTilgang: Boolean
    )
}
