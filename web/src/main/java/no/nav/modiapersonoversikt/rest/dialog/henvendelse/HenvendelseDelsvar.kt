package no.nav.modiapersonoversikt.rest.dialog.henvendelse

import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.legacy.api.utils.RestUtils
import no.nav.modiapersonoversikt.rest.dialog.apis.DelsvarRestRequest
import no.nav.modiapersonoversikt.rest.dialog.apis.DialogDelsvarApi
import no.nav.modiapersonoversikt.service.henvendelse.DelsvarRequest.DelsvarRequestBuilder
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import javax.servlet.http.HttpServletRequest

class HenvendelseDelsvar(
    private val delsvarService: no.nav.modiapersonoversikt.service.henvendelse.DelsvarService
) : DialogDelsvarApi {
    override fun svarDelvis(
        httpRequest: HttpServletRequest,
        fnr: String,
        request: DelsvarRestRequest
    ): ResponseEntity<Void> {
        val saksbehandlersValgteEnhet = RestUtils.hentValgtEnhet(request.enhet, httpRequest)

        val delsvarRequest = DelsvarRequestBuilder()
            .withFodselsnummer(fnr)
            .withTraadId(request.traadId)
            .withBehandlingsId(request.behandlingsId)
            .withSvar(request.fritekst)
            .withNavIdent(AuthContextUtils.requireIdent())
            .withValgtEnhet(saksbehandlersValgteEnhet)
            .withTemagruppe(request.temagruppe)
            .withOppgaveId(request.oppgaveId)
            .build()

        try {
            delsvarService.svarDelvis(delsvarRequest)
        } catch (exception: RuntimeException) {
            throw handterRuntimeFeil(exception)
        }

        return ResponseEntity(HttpStatus.OK)
    }

    private fun handterRuntimeFeil(exception: RuntimeException): RuntimeException {
        logger.error("Feil ved opprettelse av delvis svar", exception)
        return exception
    }

    companion object {
        private val logger = LoggerFactory.getLogger(HenvendelseDelsvar::class.java)
    }
}
