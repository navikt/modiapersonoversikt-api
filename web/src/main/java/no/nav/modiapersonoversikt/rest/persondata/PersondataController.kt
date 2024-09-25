package no.nav.modiapersonoversikt.rest.persondata

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentidenter.Identliste
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/v2/person/{fnr}")
class PersondataController(
    private val persondataService: PersondataService,
    private val tilgangskontroll: Tilgangskontroll,
    private val pdlOppslagService: PdlOppslagService,
) {
    @GetMapping
    fun hentPersondata(
        @PathVariable("fnr") fnr: String,
    ): Persondata.Data =
        tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Personalia, AuditIdentifier.FNR to fnr)) {
                persondataService.hentPerson(fnr)
            }

    @GetMapping("/identer")
    fun hentIdenter(
        @PathVariable("fnr") fodselsnummer: String,
    ): Identliste? = pdlOppslagService.hentIdenter(fodselsnummer)

    @GetMapping("/aktorid")
    fun hentAktorId(
        @PathVariable("fnr") fodselsnummer: String,
    ): String? = pdlOppslagService.hentAktorId(fodselsnummer)
}
