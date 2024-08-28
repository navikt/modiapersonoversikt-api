package no.nav.modiapersonoversikt.rest.skatteetaten.innkreving

import no.nav.modiapersonoversikt.rest.skatteetaten.innkreving.json.KravDetaljerJsonResponse
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.KravId
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.KravService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/skatteetaten/innkreving")
class KravController(
    private val kravService: KravService,
) {
    @GetMapping("/kravdetaljer/{kravidentifikator}")
    fun getKravdetaljer(
        @PathVariable kravidentifikator: String,
    ): ResponseEntity<KravDetaljerJsonResponse> {
        val kravdetaljer = kravService.hentKrav(KravId(kravidentifikator))

        return if (kravdetaljer == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(KravDetaljerJsonResponse.fromDomain(kravdetaljer))
        }
    }
}
