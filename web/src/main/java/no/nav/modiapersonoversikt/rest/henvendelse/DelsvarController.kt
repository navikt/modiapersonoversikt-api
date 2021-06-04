package no.nav.modiapersonoversikt.rest.henvendelse

import no.nav.common.auth.subject.SubjectHandler
import no.nav.modiapersonoversikt.api.utils.RestUtils
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.*
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person.Henvendelse
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.henvendelse.DelsvarRequest.DelsvarRequestBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/rest/dialog/{fnr}")
class DelsvarController @Autowired constructor(
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
                val saksbehandlersValgteEnhet = RestUtils.hentValgtEnhet(request.enhet, httpRequest)

                val delsvarRequest = DelsvarRequestBuilder()
                    .withFodselsnummer(fnr)
                    .withTraadId(request.traadId)
                    .withBehandlingsId(request.behandlingsId)
                    .withSvar(request.fritekst)
                    .withNavIdent(SubjectHandler.getIdent().get())
                    .withValgtEnhet(saksbehandlersValgteEnhet)
                    .withTemagruppe(request.temagruppe)
                    .withOppgaveId(request.oppgaveId)
                    .build()

                try {
                    delsvarService.svarDelvis(delsvarRequest)
                } catch (exception: RuntimeException) {
                    throw handterRuntimeFeil(exception)
                }

                ResponseEntity(HttpStatus.OK)
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

data class DelsvarRestRequest(
    val enhet: String?,
    val fritekst: String,
    val traadId: String,
    val behandlingsId: String,
    val temagruppe: String,
    val oppgaveId: String
)
