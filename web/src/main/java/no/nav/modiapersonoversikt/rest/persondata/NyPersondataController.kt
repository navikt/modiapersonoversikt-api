package no.nav.modiapersonoversikt.rest.persondata

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentIdenter
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/v2/person")
class NyPersondataController(
    private val persondataService: PersondataService,
    private val tilgangskontroll: Tilgangskontroll,
    private val pdlOppslagService: PdlOppslagService
) {

    @PostMapping
    fun hentPersondata(@RequestBody fnr: String): Persondata.Data {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Personalia, AuditIdentifier.FNR to fnr)) {
                persondataService.hentPerson(fnr)
            }
    }

    @PostMapping("/identer")
    fun hentIdenter(@RequestBody fnr: String): HentIdenter.Identliste? {
        return pdlOppslagService.hentIdenter(fnr)
    }

    @PostMapping("/aktorid")
    fun hentAktorId(@RequestBody fnr: String): String? {
        return pdlOppslagService.hentAktorId(fnr)
    }
}
