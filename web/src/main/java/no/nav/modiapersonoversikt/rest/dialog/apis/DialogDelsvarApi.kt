package no.nav.modiapersonoversikt.rest.dialog.apis

import org.springframework.http.ResponseEntity
import javax.servlet.http.HttpServletRequest

interface DialogDelsvarApi {
    fun svarDelvis(
        httpRequest: HttpServletRequest,
        fnr: String,
        request: DelsvarRestRequest
    ): ResponseEntity<Void>
}

data class DelsvarRestRequest(
    val enhet: String?,
    val fritekst: String,
    val traadId: String,
    val behandlingsId: String,
    val temagruppe: String,
    val oppgaveId: String
)
