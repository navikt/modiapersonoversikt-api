package no.nav.modiapersonoversikt.rest

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.commondomain.FnrRequest
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.varsel.VarslerService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest")
class VarslerController(
    private val varslerService: VarslerService,
    private val tilgangskontroll: Tilgangskontroll,
) {

    @PostMapping("/varsler")
    fun hentLegacyVarsler(@RequestBody fnrRequest: FnrRequest): List<VarslerService.Varsel> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
            .get(Audit.describe(Action.READ, Person.Varsler, AuditIdentifier.FNR to fnrRequest.fnr)) {
                varslerService.hentLegacyVarsler(Fnr(fnrRequest.fnr))
            }
    }

    @PostMapping("/v2/varsler")
    fun hentAlleVarsler(@RequestBody fnrRequest: FnrRequest): VarslerService.Result {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
            .get(Audit.describe(Action.READ, Person.Varsler, AuditIdentifier.FNR to fnrRequest.fnr)) {
                varslerService.hentAlleVarsler(Fnr(fnrRequest.fnr))
            }
    }
}
