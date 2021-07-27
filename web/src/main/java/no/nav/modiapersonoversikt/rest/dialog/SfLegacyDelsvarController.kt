package no.nav.modiapersonoversikt.rest.dialog

import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.*
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person.Henvendelse
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.NotSupportedException

@RestController
@RequestMapping("/rest/sf-legacy-dialog/{fnr}")
class SfLegacyDelsvarController @Autowired constructor(
    private val tilgangskontroll: Tilgangskontroll,
    private val delsvarService: no.nav.modiapersonoversikt.service.henvendelse.DelsvarService
) {
    @PostMapping("/delvis-svar")
    fun svarDelvis(
        httpRequest: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody request: DelsvarRestRequest
    ): ResponseEntity<Void> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(CREATE, Henvendelse.Delsvar, AuditIdentifier.FNR to fnr, AuditIdentifier.BEHANDLING_ID to request.behandlingsId)) {
                throw NotSupportedException("Delvar er ikke støttet av Salesforce")
            }
    }

    private fun handterRuntimeFeil(exception: RuntimeException): RuntimeException {
        logger.error("Feil ved opprettelse av delvis svar", exception)
        return exception
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DelsvarController::class.java)
    }
}
