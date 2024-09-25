package no.nav.modiapersonoversikt.rest.dialog.salesforce

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.NotSupportedException
import no.nav.modiapersonoversikt.rest.dialog.apis.DelsvarRestRequest
import no.nav.modiapersonoversikt.rest.dialog.apis.DialogDelsvarApi
import org.springframework.http.ResponseEntity

class SfLegacyDelsvarController : DialogDelsvarApi {
    override fun svarDelvis(
        httpRequest: HttpServletRequest,
        fnr: String,
        request: DelsvarRestRequest,
    ): ResponseEntity<Void> = throw NotSupportedException("Delsvar er ikke støttet av Salesforce")
}
