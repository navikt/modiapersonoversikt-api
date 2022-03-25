package no.nav.modiapersonoversikt.rest.saker

import com.expediagroup.graphql.types.GraphQLResponse
import no.nav.modiapersonoversikt.consumer.saf.generated.Hentbrukerssaker
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/saf/")
class SafDebugController @Autowired constructor(
    val tilgangskontroll: Tilgangskontroll,
    private val safService: SafService
) {
    @GetMapping("/{ident}/fnr")
    fun hentSafSakerFnr(@PathVariable("ident") ident: String): GraphQLResponse<Hentbrukerssaker.Result> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(ident))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Saker, AuditIdentifier.FNR to ident)) {
                safService.hentSaker(ident)
            }
    }

    @GetMapping("/{ident}/aktorid")
    fun hentSafSakerAktorId(@PathVariable("ident") ident: String): GraphQLResponse<Hentbrukerssaker.Result> {
        return tilgangskontroll
            .check(Policies.tilgangTilBrukerMedAktorId.with(ident))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Saker, AuditIdentifier.FNR to ident)) {
                safService.hentSaker(ident)
            }
    }
}
