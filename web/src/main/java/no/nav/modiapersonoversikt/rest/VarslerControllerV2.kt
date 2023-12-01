package no.nav.modiapersonoversikt.rest

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.varsel.VarslerService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest")
class VarslerControllerV2(
    private val varslerService: VarslerService,
    private val tilgangskontroll: Tilgangskontroll,
) {

    @PostMapping("/varsler")
    fun hentLegacyVarsler(@RequestBody fnr: String): List<VarslerService.Varsel> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(Action.READ, Person.Varsler, AuditIdentifier.FNR to fnr)) {
                varslerService.hentLegacyVarsler(Fnr(fnr))
            }
    }

    @PostMapping("/v2/varsler/")
    fun hentAlleVarsler(@RequestBody fnr: String): VarslerService.Result {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(Action.READ, Person.Varsler, AuditIdentifier.FNR to fnr)) {
                varslerService.hentAlleVarsler(Fnr(fnr))
            }
    }
}
