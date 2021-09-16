package no.nav.modiapersonoversikt.rest.persondata

import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/v2/person/{fnr}")
class PersondataController(
    private val persondataService: PersondataService,
    private val tilgangskontroll: Tilgangskontroll
) {

    @GetMapping
    fun hentPersondata(@PathVariable("fnr") fnr: String): Persondata.Data {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Personalia, AuditIdentifier.FNR to fnr)) {
                persondataService.hentPerson(fnr)
            }
    }
}
