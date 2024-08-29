package no.nav.modiapersonoversikt.rest.skatteetaten.innkreving

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.rest.common.FnrRequest
import no.nav.modiapersonoversikt.rest.skatteetaten.innkreving.json.KravdetaljerJsonResponse
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.KravService
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.KravdetaljerId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/skatteetaten/innkreving")
class KravController(
    private val kravService: KravService,
) {
    @GetMapping("/kravdetaljer/{kravidentifikator}")
    fun hentKravdetaljer(
        @PathVariable kravidentifikator: String,
    ): ResponseEntity<KravdetaljerJsonResponse> {
        val kravdetaljer = kravService.hentKravdetaljer(KravdetaljerId(kravidentifikator))

        return if (kravdetaljer == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(KravdetaljerJsonResponse.fromDomain(kravdetaljer))
        }
    }

    @PostMapping("/kravdetaljer")
    fun hentAlleKravdetaljer(
        @RequestBody fnrRequest: FnrRequest,
    ): ResponseEntity<List<KravdetaljerJsonResponse>> {
        val fnr = Fnr(fnrRequest.fnr)

        if (!Fnr.isValid(fnr.get())) {
            return ResponseEntity.badRequest().build()
        }

        val kravdetaljer = kravService.hentAlleKravdetaljer(fnr)

        return ResponseEntity.ok(kravdetaljer.map(KravdetaljerJsonResponse::fromDomain))
    }
}
