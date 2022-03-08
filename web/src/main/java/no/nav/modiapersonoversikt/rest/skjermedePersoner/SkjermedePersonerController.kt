package no.nav.modiapersonoversikt.rest.skjermedePersoner

import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.READ
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Saksbehandler
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/skjermetperson/{fnr}")
class SkjermedePersonerController @Autowired constructor(
    private val skjermedePersonerApi: SkjermedePersonerApi,
    private val tilgangskontroll: Tilgangskontroll
) {
    @GetMapping
    fun erSkjermetPerson(@PathVariable("fnr") fnr: String): Map<String, Boolean> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(READ, Saksbehandler.SkjermetPerson, AuditIdentifier.FNR to fnr)) {
                mapOf("erSkjermetPerson" to skjermedePersonerApi.erSkjermetPerson(fnr))
            }
    }
}
