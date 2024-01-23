package no.nav.modiapersonoversikt.rest.saker

import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.commondomain.sak.Baksystem
import no.nav.modiapersonoversikt.commondomain.sak.Feilmelding
import no.nav.modiapersonoversikt.commondomain.sak.ResultatWrapper
import no.nav.modiapersonoversikt.commondomain.sak.TjenesteResultatWrapper
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.READ
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.journalforingsaker.SakerService
import no.nav.modiapersonoversikt.service.saf.SafService
import no.nav.modiapersonoversikt.service.saf.domain.Dokument.Variantformat
import no.nav.modiapersonoversikt.service.saf.domain.Dokument.Variantformat.ARKIV
import no.nav.modiapersonoversikt.service.saf.domain.DokumentMetadata
import no.nav.modiapersonoversikt.service.sakstema.SakstemaService
import no.nav.modiapersonoversikt.service.sakstema.domain.Sak
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
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
    fun hentSakstema(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestParam(value = "enhet") enhet: String
    ): SakerApi.Resultat {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(READ, AuditResources.Person.Saker, AuditIdentifier.FNR to fnr)) {
                val sakerWrapper = sakerService.hentSafSaker(fnr).asWrapper()
                val sakstemaWrapper = sakstemaService.hentSakstema(sakerWrapper.resultat, fnr)

                val mappingContext = SakerApiMapper.createMappingContext(
                    tilgangskontroll = tilgangskontroll,
                    enhet = EnhetId(enhet),
                    sakstemaer = sakstemaWrapper.resultat,
                )

                mappingContext.mapTilResultat(sakstemaWrapper.resultat)
            }
    }

    @GetMapping("/v2/sakstema")
    fun hentSakstemaSoknadsstatus(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestParam(value = "enhet") enhet: String
    ): SakerApi.ResultatSoknadsstatus {
        return tilgangskontroll.check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(READ, AuditResources.Person.Saker, AuditIdentifier.FNR to fnr)) {
                val sakerWrapper = sakerService.hentSafSaker(fnr).asWrapper()
                val sakstemaWrapper = sakstemaService.hentSakstemaSoknadsstatus(sakerWrapper.resultat, fnr)
                val mappingContext = SakerApiMapper.createMappingContext(
                    tilgangskontroll = tilgangskontroll,
                    enhet = EnhetId(enhet),
                    sakstemaer = sakstemaWrapper.resultat
                )

                mappingContext.mapTilResultat(sakstemaWrapper.resultat)
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
            .get(
                Audit.describe(
                    READ,
                    AuditResources.Person.Dokumenter,
                    AuditIdentifier.FNR to fnr,
                    AuditIdentifier.JOURNALPOST_ID to journalpostId,
                    AuditIdentifier.DOKUMENT_REFERERANSE to dokumentreferanse
                )
            ) {
                val journalpostMetadata = hentDokumentMetadata(journalpostId, fnr)
                val tilgangskontrollResult = harTilgangTilDokument(fnr, journalpostMetadata)

                // TODO erstatt tilgangsstyring
                if (!tilgangskontrollResult.result.isPresent || !finnesDokumentReferansenIMetadata(
                        journalpostMetadata,
                        dokumentreferanse
                    )
                ) {
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

    private fun hentDokumentMetadata(journalpostId: String, fnr: String): DokumentMetadata {
        return safService.hentJournalposter(fnr).resultat
            .firstOrNull { dokumentMetadata -> journalpostId == dokumentMetadata.journalpostId }
            ?: throw RuntimeException("Fant ikke metadata om journalpostId $journalpostId. Dette bør ikke skje.")
    }

    private fun finnesDokumentReferansenIMetadata(
        dokumentMetadata: DokumentMetadata,
        dokumentreferanse: String
    ): Boolean {
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
}
