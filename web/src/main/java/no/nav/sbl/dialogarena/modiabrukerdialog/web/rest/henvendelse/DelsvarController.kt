package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse

import no.nav.common.auth.subject.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.DelsvarRequest.DelsvarRequestBuilder
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.DelsvarService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import javax.servlet.http.HttpServletRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.*
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import no.nav.sbl.dialogarena.naudit.AuditResources.Person.Henvendelse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dialog/{fnr}")
class DelsvarController @Autowired constructor(
        private val tilgangskontroll: Tilgangskontroll,
        private val delsvarService: DelsvarService
) {
    @PostMapping("/delvis-svar")
    fun svarDelvis(
            @PathVariable("fnr") fnr: String,
            httpRequest: HttpServletRequest,
            request: DelsvarRestRequest): ResponseEntity<Void>
    {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get(Audit.describe(CREATE, Henvendelse.Delsvar, AuditIdentifier.FNR to fnr, AuditIdentifier.BEHANDLING_ID to request.behandlingsId)) {
                    val saksbehandlersValgteEnhet = RestUtils.hentValgtEnhet(httpRequest)

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
        val fritekst: String,
        val traadId: String,
        val behandlingsId: String,
        val temagruppe: String,
        val oppgaveId: String
)
