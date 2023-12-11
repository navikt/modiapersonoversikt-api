package no.nav.modiapersonoversikt.rest.persondata

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.commondomain.FnrRequest
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentIdenter
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/v3/person")
class PersondataControllerV2(
    private val persondataService: PersondataService,
    private val tilgangskontroll: Tilgangskontroll,
    private val pdlOppslagService: PdlOppslagService
) {

    @PostMapping
    fun hentPersondata(@RequestBody fnrRequest: FnrRequest): Persondata.Data {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Personalia, AuditIdentifier.FNR to fnrRequest.fnr)) {
                persondataService.hentPerson(fnrRequest.fnr)
            }
    }

    @PostMapping("/identer")
    fun hentIdenter(@RequestBody fnrRequest: FnrRequest): HentIdenter.Identliste? {
        return pdlOppslagService.hentIdenter(fnrRequest.fnr)
    }

    @PostMapping("/aktorid")
    fun hentAktorId(@RequestBody fnrRequest: FnrRequest): String? {
        return pdlOppslagService.hentAktorId(fnrRequest.fnr)
    }
}
