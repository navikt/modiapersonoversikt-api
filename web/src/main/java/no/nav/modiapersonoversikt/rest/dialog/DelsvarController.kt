package no.nav.modiapersonoversikt.rest.dialog

import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.*
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person.Henvendelse
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.rest.dialog.apis.DelsvarRestRequest
import no.nav.modiapersonoversikt.rest.dialog.apis.DialogDelsvarApi
import no.nav.modiapersonoversikt.service.unleash.Feature
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/rest/dialog/{fnr}")
class DelsvarController @Autowired constructor(
    private val tilgangskontroll: Tilgangskontroll,
    private val delsvarApi: DialogDelsvarApi
) {
    @PostMapping("/delvis-svar")
    fun svarDelvis(
        httpRequest: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody request: DelsvarRestRequest
    ): ResponseEntity<Void> {
        return tilgangskontroll
            .check(Policies.featureToggleDisabled.with(Feature.STENG_STO.propertyKey))
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(CREATE, Henvendelse.Delsvar, AuditIdentifier.FNR to fnr, AuditIdentifier.BEHANDLING_ID to request.behandlingsId)) {
                delsvarApi.svarDelvis(httpRequest, fnr, request)
            }
    }
}
