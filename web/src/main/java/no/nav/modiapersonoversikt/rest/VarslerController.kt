package no.nav.modiapersonoversikt.rest

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.varsel.VarslerService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest")
class VarslerController(
    private val varslerService: VarslerService,
    private val tilgangskontroll: Tilgangskontroll,
) {

    @GetMapping("/varsler/{fnr}")
    fun hentLegacyVarsler(@PathVariable("fnr") fnr: String): List<VarslerService.Varsel> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(Action.READ, Person.Varsler, AuditIdentifier.FNR to fnr)) {
                varslerService.hentLegacyVarsler(Fnr(fnr))
            }
    }

    @GetMapping("/v2/varsler/{fnr}")
    fun hentAlleVarsler(@PathVariable("fnr") fnr: String): List<VarslerService.UnifiedVarsel> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(Action.READ, Person.Varsler, AuditIdentifier.FNR to fnr)) {
                varslerService.hentAlleVarsler(Fnr(fnr))
            }
    }
}
