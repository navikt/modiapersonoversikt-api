package no.nav.modiapersonoversikt.rest.skatteetaten.innkreving

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.rest.common.FnrRequest
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.InnkrevingskravId
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.InnkrevingskravService
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.Krav
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/innkrevingskrav")
class InnkrevingskravController(
    private val innkrevingskravService: InnkrevingskravService,
    private val tilgangskontroll: Tilgangskontroll,
) {
    @GetMapping("/{innkrevingskravId}")
    fun hentInnkrevingskrav(
        @PathVariable innkrevingskravId: String,
    ): ResponseEntity<Krav?> =
        tilgangskontroll
            .check(Policies.tilgangTilInnkrevingskrav())
            .get(
                Audit.describe(
                    Audit.Action.READ,
                    AuditResources.Innkrevingskrav,
                    AuditIdentifier.INNKREVINGSKRAV_ID to innkrevingskravId,
                ),
            ) {
                val innkrevingskrav = innkrevingskravService.hentInnkrevingskrav(InnkrevingskravId(innkrevingskravId))
                if (innkrevingskrav == null) {
                    ResponseEntity.notFound().build()
                } else {
                    ResponseEntity.ok(innkrevingskrav)
                }
            }

    @PostMapping
    fun hentAlleInnkrevingskrav(
        @RequestBody fnrRequest: FnrRequest,
    ): ResponseEntity<List<Krav>> =
        tilgangskontroll
            .check(Policies.tilgangTilInnkrevingskrav())
            .get(
                Audit.describe(
                    Audit.Action.READ,
                    AuditResources.Innkrevingskrav,
                    AuditIdentifier.FNR to fnrRequest.fnr,
                ),
            ) {
                val fnr = Fnr(fnrRequest.fnr)
                if (!Fnr.isValid(fnr.get())) {
                    return@get ResponseEntity.badRequest().build()
                }
                val innkrevingskrav = innkrevingskravService.hentAlleInnkrevingskrav(fnr)
                ResponseEntity.ok(innkrevingskrav)
            }
}
